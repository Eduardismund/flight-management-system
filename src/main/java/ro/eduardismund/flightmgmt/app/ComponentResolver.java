package ro.eduardismund.flightmgmt.app;

public interface ComponentResolver {
    @SuppressWarnings("unused")
    <T> T resolveComponent(Class<T> cls);
}
