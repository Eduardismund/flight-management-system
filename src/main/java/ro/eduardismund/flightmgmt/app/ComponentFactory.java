package ro.eduardismund.flightmgmt.app;

public interface ComponentFactory<T> {
    T createComponent(ApplicationContext context);
}
