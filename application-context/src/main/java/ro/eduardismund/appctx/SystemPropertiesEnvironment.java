package ro.eduardismund.appctx;

/**
 * Environment implementation that retrieves properties from system properties.
 */
public enum SystemPropertiesEnvironment implements Environment {
    SYS_PROP_ENV;

    /**
     * Gets a system property by name.
     *
     * @param propertyName The name of the property.
     * @return The property value, or null if not found.
     */
    @Override
    public String getProperty(String propertyName) {
        return System.getProperty(propertyName);
    }
}
