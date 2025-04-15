package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.github.eduardismund.appctx.ComponentResolver;
import com.github.eduardismund.appctx.Environment;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.server.Logger;

class LoggerComponentFactoryTest {

    @Test
    void createComponent() {
        LoggerComponentFactory loggerCompFact = new LoggerComponentFactory();
        final var properties = mock(Environment.class);
        final var componentResolver = mock(ComponentResolver.class);
        final var component = loggerCompFact.createComponent(properties, componentResolver);
        assertInstanceOf(Logger.class, component);

        assertInstanceOf(PrintStream.class, component.out);
    }
}
