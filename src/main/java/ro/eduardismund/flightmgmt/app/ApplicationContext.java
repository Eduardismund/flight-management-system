package ro.eduardismund.flightmgmt.app;

import static ro.eduardismund.flightmgmt.app.SystemPropertiesEnvironment.SYS_PROP_ENV;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * The ApplicationContext class is responsible for managing and processing the components in the application.
 * It registers components, resolves dependencies, and creates
 * instances of components based on their conditions and configurations.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public class ApplicationContext {

    static final String CONFIG_FILE = "config/application.properties";
    final Map<Class<?>, Object> components = new HashMap<>();
    final Set<ComponentCondition> componentClasses = new HashSet<>();

    @Getter
    private Environment properties;

    /**
     * Registers a component instance in the context.
     *
     * @param componentInstance The component instance to be registered.
     */
    @SuppressWarnings("unused")
    public void registerComponent(Object componentInstance) {
        components.put(componentInstance.getClass(), componentInstance);
    }

    /**
     * Loads the configuration file and returns an environment instance containing the properties.
     *
     * @return An {@link Environment} instance populated with properties from the configuration file.
     */
    @SneakyThrows
    Environment loadConfigFile() {
        final var properties = new Properties();
        try (var buffer = getReader()) {
            properties.load(buffer);
        }
        return new PropertiesEnvironment(properties);
    }

    Reader getReader() throws IOException {
        return getReader(CONFIG_FILE);
    }

    Reader getReader(String configFile) throws IOException {
        return Files.newBufferedReader(Path.of(configFile));
    }

    /**
     * Registers a component class and its associated condition in the context.
     *
     * @param componentClass The class of the component to be registered.
     * @param condition      The condition that determines when the component should be created.
     */
    public void registerComponentClass(Class<?> componentClass, Condition condition) {
        this.componentClasses.add(new ComponentCondition(componentClass, condition));
    }

    /**
     * Registers a component class with a default condition that always returns {@code true}.
     *
     * @param componentClass The class of the component to be registered.
     */
    public void registerComponentClass(Class<?> componentClass) {
        registerComponentClass(componentClass, Condition.alwaysTrue());
    }

    /**
     * Processes the registered components by creating instances of them based on their conditions and configurations.
     * Components are processed in two stages: first, component factories are handled, and then other components.
     */
    public void processComponents() {
        properties = new CompositeEnvironment(List.of(SYS_PROP_ENV, loadConfigFile()));
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

    /**
     * Resolves a dependency by looking for a registered component or creating a new one.
     *
     * @param <T>  The type of the dependency.
     * @param type The class type of the dependency to resolve.
     * @return The resolved dependency of the specified type.
     * @throws IllegalStateException If the dependency cannot be resolved.
     */
    @SuppressWarnings("unchecked")
    <T> T resolveDependency(Class<T> type) {
        if (components.containsKey(type)) {
            return (T) components.get(type);
        }

        for (final Map.Entry<Class<?>, Object> entry : components.entrySet()) {
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

    /**
     * Creates an instance of a component class by calling the appropriate constructor.
     *
     * @param cls The class of the component to create.
     * @return The created component instance.
     */
    private Object createComponent(Class<?> cls) {

        return Arrays.stream(cls.getConstructors())
                .map(constructor -> putComponent(cls, constructor))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No public constructor found for " + cls));
    }

    /**
     * Instantiates a component using the specified constructor and resolves its dependencies.
     *
     * @param cls         The class of the component.
     * @param constructor The constructor to use for instantiation.
     * @return The newly created component instance.
     */
    @SneakyThrows
    Object putComponent(Class<?> cls, Constructor<?> constructor) {
        final var newComp = callConstructor(constructor);

        if (newComp instanceof ComponentFactory) {
            final var actualComponent =
                    ((ComponentFactory<?>) newComp).createComponent(properties, this::resolveDependency);
            components.put(actualComponent.getClass(), actualComponent);
        } else {
            components.put(cls, newComp);
        }
        return newComp;
    }

    /**
     * Calls the constructor of a component and resolves its dependencies.
     *
     * @param constructor The constructor to call.
     * @return The created component instance.
     * @throws InstantiationException    If the component cannot be instantiated.
     * @throws IllegalAccessException    If access to the constructor is restricted.
     * @throws InvocationTargetException If an exception is thrown by the constructor.
     */
    Object callConstructor(Constructor<?> constructor)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object[] parameters = new Object[constructor.getParameterCount()];
        int index = 0;

        for (final var param : constructor.getParameters()) {
            parameters[index++] = resolveDependency(param.getType());
        }

        return constructor.newInstance(parameters);
    }

    /**
     * Runs the registered {@link ApplicationRunnable} components with the specified arguments.
     *
     * @param args The arguments to pass to the run method of the components.
     */
    public void run(String... args) {
        for (final Object component : components.values()) {
            if (component instanceof ApplicationRunnable) {
                ((ApplicationRunnable) component).run(args);
            }
        }
    }

    record ComponentCondition(Class<?> componentClass, Condition condition) {}
}
