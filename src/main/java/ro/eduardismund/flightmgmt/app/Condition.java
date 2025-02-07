package ro.eduardismund.flightmgmt.app;

/**
 * Represents a condition that can be tested against an environment.
 */
public interface Condition {

    /**
     * Creates a condition that checks if a property equals a given value.
     *
     * @param propertyName The name of the property to check.
     * @param expectedValue The value to compare the property against.
     * @return A new condition that tests for the property equality.
     */
    static Condition propertyEquals(String propertyName, String expectedValue) {
        return new PropertyEqualsCondition(propertyName, expectedValue);
    }

    /**
     * Creates a condition that is always true.
     *
     * @return A condition that always returns true.
     */
    static Condition alwaysTrue() {
        return AlwaysTrueCondition.TRUE_CONDITION;
    }

    /**
     * Negates the current condition.
     *
     * @return A new condition that represents the negation of this condition.
     */
    default Condition negate() {
        return new NegateCondition(this);
    }

    /**
     * Tests the condition against the given environment.
     *
     * @param properties The environment properties to test the condition with.
     * @return true if the condition is met, false otherwise.
     */
    boolean test(Environment properties);

    /**
     * Combines this condition with another using a logical OR.
     *
     * @param other The other condition to combine with.
     * @return A new condition that represents the logical OR of the two conditions.
     */
    default Condition or(Condition other) {
        return new OrCondition(this, other);
    }
}
