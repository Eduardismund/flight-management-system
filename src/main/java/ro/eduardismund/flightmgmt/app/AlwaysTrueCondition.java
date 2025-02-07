package ro.eduardismund.flightmgmt.app;

/**
 * This enum represents a condition that always evaluates to true.
 * It implements the {@link Condition} interface and provides an implementation
 * of the {@link Condition#test(Environment)} method
 * that always returns {@code true}.
 */
public enum AlwaysTrueCondition implements Condition {
    TRUE_CONDITION;

    /**
     * Evaluates the condition with the given environment properties.
     *
     * @param properties The environment properties.
     * @return {@code true} regardless of the input.
     */
    @Override
    public boolean test(Environment properties) {
        return true;
    }
}
