package ro.eduardismund.flightmgmt.app;

public enum AlwaysTrueCondition implements Condition {
    ALWAYS_TRUE_CONDITION;

    @Override
    public boolean test(Environment properties) {
        return true;
    }
}
