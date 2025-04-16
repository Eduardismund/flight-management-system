package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.server.Logger;

class LoggerSupplierTest {

    @Test
    void get() {
        LoggerSupplier loggerCompFact = new LoggerSupplier();

        final var component = loggerCompFact.get();
        assertInstanceOf(Logger.class, component);

        assertInstanceOf(PrintStream.class, component.out);
    }
}
