package ro.eduardismund.flightmgmt.app;

import java.util.Properties;

public interface Condition {
    static Condition propertyEquals(String propertyName, String expectedValue) {
        return new PropertyEqualsCondition(propertyName, expectedValue);
    }

    static Condition alwaysTrue() {
        return AlwaysTrueCondition.ALWAYS_TRUE_CONDITION;
    }

    default Condition negate() {
        return new NegateCondition(this);
    }

    boolean test(Properties properties);
}
