package ro.eduardismund.flightmgmt.app;

/**
 * Interface for resolving components of a given type.
 *
 */
public interface ComponentResolver {

    /**
     * Resolves and returns a component of the specified type.
     *
     * @param cls The class type of the component to resolve.
     * @param <T> The type of the component.
     * @return The resolved component of type {@link T}.
     */
    @SuppressWarnings("unused")
    <T> T resolveComponent(Class<T> cls);
}
