package ro.eduardismund.flightmgmt.app;

public record PropertyEqualsCondition(String propertyName, String expectedValue) implements Condition {
    @Override
    public boolean test(Environment properties) {
        return properties.getProperty(propertyName).equals(expectedValue);
    }
}
