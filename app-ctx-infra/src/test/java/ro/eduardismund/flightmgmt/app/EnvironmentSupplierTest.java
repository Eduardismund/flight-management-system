package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.StandardEnvironment;

class EnvironmentSupplierTest {

    @Test
    void get_isSuccessful() {
        final var subject = new EnvironmentSupplier();

        final var actual = subject.get();

        assertNotNull(actual);
        assertInstanceOf(StandardEnvironment.class, actual);
        assertEquals("bar", actual.getRequiredProperty("foo"));
    }

    @Test
    void get_throwsIoException() throws IOException {
        final var subject = spy(new EnvironmentSupplier());

        final var exception = new IOException("test");
        doThrow(exception).when(subject).createResourcePropertySource();
        assertSame(exception, assertThrows(IOException.class, subject::get));
    }
}
