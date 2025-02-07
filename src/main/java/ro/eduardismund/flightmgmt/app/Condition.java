package ro.eduardismund.flightmgmt.app;

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

    boolean test(Environment properties);

    default Condition or(Condition other) {
        return new OrCondition(this, other);
    }
}
