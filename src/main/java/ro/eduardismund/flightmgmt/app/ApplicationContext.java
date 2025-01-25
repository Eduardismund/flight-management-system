package ro.eduardismund.flightmgmt.app;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final Map<Class<?>, Object> components = new HashMap<>();
    private final Set<Class<?>> componentClasses = new HashSet<>();


    public void registerComponent(Object componentInstance) {
        components.put(componentInstance.getClass(), componentInstance);
    }

    public void registerComponentClass(Class<?> componentClass) {
        this.componentClasses.add(componentClass);
    }

    public void processComponents() {
        componentClasses.forEach(this::createComponent);
    }

    private Object resolveDependency(Class<?> type) {
        if (components.containsKey(type)) {
            return components.get(type);
        }

        for (Map.Entry<Class<?>, Object> entry : components.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }

        return componentClasses.stream().filter(type::isAssignableFrom).map(this::createComponent).findFirst().orElse(null);

    }

    @SneakyThrows
    private Object createComponent(Class<?> cls) {
        final var existingComponent = components.get(cls);
        if (existingComponent != null) {
            return existingComponent;
        }

        for (final var constructor : cls.getConstructors()) {

            Object[] parameters = new Object[constructor.getParameterCount()];
            int index = 0;

            for (final var param : constructor.getParameters()) {
                parameters[index++] = resolveDependency(param.getType());
                if(parameters[index-1] == null) {
                    throw new IllegalStateException("Couldn't resolve dependency of type "  + param.getType() + " for " + cls.getName());
                }
            }

            final var newComp = constructor.newInstance(parameters);
            components.put(cls, newComp);
            return newComp;

        }


        throw new IllegalStateException("No public constructor found for " + cls);
    }


    private String fieldValues(Object component) {
        final var fields = component.getClass().getDeclaredFields();
        return Arrays.stream(fields).filter(field -> !Modifier.isStatic(field.getModifiers())).map(field -> field.getName() + ": " + getFieldValue(component, field)).collect(Collectors.joining(", "));
    }

    @SneakyThrows
    private static Object getFieldValue(Object component, Field field) {
        try{
            field.setAccessible(true);
        } catch (Exception e){
            return "Not accessible!";
        }
        return field.get(component);
    }

    public void run(String[] args) {
        for (Object component : components.values()) {
            if (component instanceof ApplicationRunnable) {
                ((ApplicationRunnable) component).run(args);
            }
        }
    }
}
