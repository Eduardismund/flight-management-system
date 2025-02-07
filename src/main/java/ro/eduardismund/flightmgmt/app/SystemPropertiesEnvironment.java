package ro.eduardismund.flightmgmt.app;

public enum SystemPropertiesEnvironment implements Environment {
    SYSTEM_PROPERTIES_ENVIRONMENT;

    @Override
    public String getProperty(String propertyName) {
        return System.getProperty(propertyName);
    }
}
