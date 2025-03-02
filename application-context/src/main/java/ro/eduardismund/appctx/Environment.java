package ro.eduardismund.appctx;

/**
 * Interface representing an environment that provides property values.
 */
public interface Environment {

    /**
     * Retrieves the value of a property.
     *
     * @param propertyName The name of the property to retrieve.
     * @return The value of the property, or {@code null} if not found.
     */
    String getProperty(String propertyName);
}
