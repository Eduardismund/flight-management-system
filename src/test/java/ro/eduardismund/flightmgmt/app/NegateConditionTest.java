package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NegateConditionTest {

    static Stream<Boolean> provideTestCases() {
        return Stream.of(true, false);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void test_negateCondition(boolean originalResult) {
        final var condition = mock(Condition.class);
        doReturn(originalResult).when(condition).test(any());
        final var negatedCondition = new NegateCondition(condition);

        final var properties = mock(Properties.class);
        assertEquals(!originalResult, negatedCondition.test(properties));
        verify(condition).test(properties);
    }
}
