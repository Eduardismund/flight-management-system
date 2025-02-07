package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConditionTest {

    @Test
    void propertyEquals() {
        assertInstanceOf(PropertyEqualsCondition.class, Condition.propertyEquals("", ""));
    }

    @Test
    void alwaysTrue() {
        assertInstanceOf(AlwaysTrueCondition.class, Condition.alwaysTrue());
    }

    @Test
    void negate() {

        assertInstanceOf(NegateCondition.class, Condition.alwaysTrue().negate());
    }

    @Test
    void orCondition() {
        assertInstanceOf(OrCondition.class, Condition.alwaysTrue().or(Condition.alwaysTrue()));
    }
}
