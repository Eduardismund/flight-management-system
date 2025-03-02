package ro.eduardismund.appctx;

/**
 * A factory interface for creating components.
 *
 * @param <T> the type of component to create
 */
public interface ComponentFactory<T> {

    /**
     * Creates a component of type T.
     *
     * @param properties The environment properties.
     * @param resolver The component resolver.
     * @return The created component.
     */
    T createComponent(Environment properties, ComponentResolver resolver);
}
