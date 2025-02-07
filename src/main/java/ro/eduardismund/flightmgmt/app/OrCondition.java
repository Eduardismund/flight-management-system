package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;

/**
 * Condition that evaluates as true if either of the two conditions is true.
 * Represents a logical OR operation between two conditions.
 */
@RequiredArgsConstructor
public class OrCondition implements Condition {
    private final Condition condition1;
    private final Condition condition2;

    @Override
    public boolean test(Environment properties) {
        return condition1.test(properties) || condition2.test(properties);
    }
}
