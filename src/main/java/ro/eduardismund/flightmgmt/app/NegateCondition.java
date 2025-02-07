package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;

/**
 * Condition that negates the result of another condition.
 * Used to invert the boolean result of a given condition.
 */
@RequiredArgsConstructor
public class NegateCondition implements Condition {

    private final Condition negatedCondition;

    @Override
    public boolean test(Environment properties) {
        return !negatedCondition.test(properties);
    }
}
