package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;

class CompositeEnvironmentTest {

    @Test
    void getProperty() {
        final var property = mock(Environment.class);
        final var test = "test";
        doReturn(test).when(property).getProperty(test);

        final var environment = List.of(property);
        final var composite = new CompositeEnvironment();
        composite.environments = environment;

        assertEquals(test, composite.getProperty(test));
    }
}
