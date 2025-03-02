package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoggerTest {
    private Logger logger;
    private PrintStream out;

    @BeforeEach
    void setUp() {
        out = mock(PrintStream.class);
        logger = new Logger(out);
    }

    @Test
    void println() {
        assertInstanceOf(Logger.class, logger.println("test"));
        verify(out).println("test");
    }

    @Test
    void printException() {
        final var exception = mock(Exception.class);
        logger.printException(exception);
        verify(exception).printStackTrace(out);
    }
}
