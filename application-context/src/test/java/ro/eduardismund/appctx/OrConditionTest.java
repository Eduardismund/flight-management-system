package ro.eduardismund.appctx;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class OrConditionTest {
    @Test
    void test_cond1Cond2True() {
        final var properties = mock(Environment.class);
        final var cond1 = mock(Condition.class);
        final var cond2 = mock(Condition.class);
        doReturn(true).when(cond1).test(properties);
        doReturn(true).when(cond2).test(properties);

        final var orCondition = new OrCondition(cond1, cond2);

        assertTrue(orCondition.test(properties));
    }

    @Test
    void test_cond1True() {
        final var properties = mock(Environment.class);
        final var cond1 = mock(Condition.class);
        final var cond2 = mock(Condition.class);
        doReturn(true).when(cond1).test(properties);

        final var orCondition = new OrCondition(cond1, cond2);

        assertTrue(orCondition.test(properties));
    }

    @Test
    void test_cond2True() {
        final var properties = mock(Environment.class);
        final var cond1 = mock(Condition.class);
        final var cond2 = mock(Condition.class);
        doReturn(true).when(cond2).test(properties);

        final var orCondition = new OrCondition(cond1, cond2);

        assertTrue(orCondition.test(properties));
    }

    @Test
    void test_condFalse() {
        final var properties = mock(Environment.class);
        final var cond1 = mock(Condition.class);
        final var cond2 = mock(Condition.class);

        final var orCondition = new OrCondition(cond1, cond2);

        assertFalse(orCondition.test(properties));
    }
}
