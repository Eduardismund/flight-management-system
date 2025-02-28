package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Properties;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class CliManagerComponentFactoryTest {

    final CliManagerComponentFactory factory = new CliManagerComponentFactory();

    @Test
    void createComponent() {
        final var properties = mock(Properties.class);
        final var resolver = mock(ComponentResolver.class);

        final var cliManager = factory.createComponent(properties, resolver);
        assertNotNull(cliManager);
        assertEquals(System.out, cliManager.out);
        assertInstanceOf(Scanner.class, cliManager.scanner);
    }
}
