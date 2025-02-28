package ro.eduardismund.flightmgmt.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import lombok.SneakyThrows;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class ApplicationContext {

    static final String CONFIG_FILE = "config/application.properties";
    final Map<Class<?>, Object> components = new HashMap<>();
    final Set<ComponentCondition> componentClasses = new HashSet<>();

    private Properties properties;

    @SuppressWarnings("unused")
    public void registerComponent(Object componentInstance) {
        components.put(componentInstance.getClass(), componentInstance);
    }

    @SneakyThrows
    Properties loadConfigFile() {
        final var properties = new Properties();
        try (final var buffer = getReader()) {
            properties.load(buffer);
        }
        return properties;
    }

    Reader getReader() throws IOException {
        return getReader(CONFIG_FILE);
    }

    Reader getReader(String configFile) throws IOException {
        return Files.newBufferedReader(Path.of(configFile));
    }

    public void registerComponentClass(Class<?> componentClass, Condition condition) {
        this.componentClasses.add(new ComponentCondition(componentClass, condition));
    }

    public void registerComponentClass(Class<?> componentClass) {
        registerComponentClass(componentClass, Condition.alwaysTrue());
    }

    public void processComponents() {
        properties = loadConfigFile();
        createComponents(ComponentFactory.class::isAssignableFrom);
        createComponents(Predicate.not(ComponentFactory.class::isAssignableFrom));
    }

    private void createComponents(Predicate<Class<?>> classPredicate) {
        componentClasses.stream()
                .filter(compCond -> classPredicate.test(compCond.componentClass))
                .filter(compCond -> compCond.condition.test(properties))
                .map(ComponentCondition::componentClass)
                .forEach(this::createComponent);
    }

    @SuppressWarnings("unchecked")
    <T> T resolveDependency(Class<T> type) {
        if (components.containsKey(type)) {
            return (T) components.get(type);
        }

        for (Map.Entry<Class<?>, Object> entry : components.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return (T) entry.getValue();
            }
        }

        return (T) componentClasses.stream()
                .filter(compCond -> type.isAssignableFrom(compCond.componentClass))
                .filter(compCond -> compCond.condition.test(properties))
                .map(ComponentCondition::componentClass)
                .map(this::createComponent)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not resolve dependency of type " + type.getName()));
    }

    @SneakyThrows
    private Object createComponent(Class<?> cls) {
        for (final var constructor : cls.getConstructors()) {

            Object[] parameters = new Object[constructor.getParameterCount()];
            int index = 0;

            for (final var param : constructor.getParameters()) {
                parameters[index++] = resolveDependency(param.getType());
            }

            final var newComp = constructor.newInstance(parameters);

            if (newComp instanceof ComponentFactory) {
                final var actualComponent =
                        ((ComponentFactory<?>) newComp).createComponent(properties, this::resolveDependency);
                components.put(actualComponent.getClass(), actualComponent);
            } else {
                components.put(cls, newComp);
            }
            return newComp;
        }

        throw new IllegalStateException("No public constructor found for " + cls);
    }

    public void run(String[] args) {
        for (Object component : components.values()) {
            if (component instanceof ApplicationRunnable) {
                ((ApplicationRunnable) component).run(args);
            }
        }
    }

    record ComponentCondition(Class<?> componentClass, Condition condition) {}
}
