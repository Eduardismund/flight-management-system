package ro.eduardismund.flightmgmt.app;

import java.util.Properties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NegateCondition implements Condition {

    private final Condition negatedCondition;

    @Override
    public boolean test(Properties properties) {
        return !negatedCondition.test(properties);
    }
}
