package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NegateCondition implements Condition {

    private final Condition negatedCondition;

    @Override
    public boolean test(Environment properties) {
        return !negatedCondition.test(properties);
    }
}
