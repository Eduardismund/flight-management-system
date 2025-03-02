package ro.eduardismund.appctx;

/**
 * {@link Condition} implementation that checks if a property equals a specified value.
 */
public record PropertyEqualsCondition(String propertyName, String expectedValue) implements Condition {

    @Override
    public boolean test(Environment properties) {
        return properties.getProperty(propertyName).equals(expectedValue);
    }
}
