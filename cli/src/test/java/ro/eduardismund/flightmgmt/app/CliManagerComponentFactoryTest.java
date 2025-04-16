package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;
import org.junit.jupiter.api.Test;

class CliManagerComponentFactoryTest {

    final CliManagerSupplier supplier = new CliManagerSupplier();

    @Test
    void get() {
        final var cliManager = supplier.get();
        assertNotNull(cliManager);
        assertEquals(System.out, cliManager.out);
        assertInstanceOf(Scanner.class, cliManager.scanner);
    }
}
