package ro.eduardismund.flightmgmt.app;

import java.util.Properties;

public record PropertyEqualsCondition(String propertyName, String expectedValue) implements Condition {
    @Override
    public boolean test(Properties properties) {
        return properties.getProperty(propertyName).equals(expectedValue);
    }
}
