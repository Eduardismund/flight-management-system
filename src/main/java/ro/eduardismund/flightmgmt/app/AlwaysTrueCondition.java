package ro.eduardismund.flightmgmt.app;

import java.util.Properties;

public enum AlwaysTrueCondition implements Condition {
    ALWAYS_TRUE_CONDITION;

    @Override
    public boolean test(Properties properties) {
        return true;
    }
}
