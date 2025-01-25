package ro.eduardismund.flightmgmt.app;

import java.util.Properties;

public interface ComponentFactory<T> {
    T createComponent(Properties properties, ComponentResolver resolver);
}
