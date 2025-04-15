package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.github.eduardismund.appctx.ComponentResolver;
import com.github.eduardismund.appctx.Environment;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;

class IfmPersistenceManagerComponentFactoryTest {

    @Test
    void createComponent() {
        final var properties = mock(Environment.class);
        doReturn("test").when(properties).getProperty("filePath");

        final var manager = new IfmPersistenceManagerComponentFactory();
        final var resolver = mock(ComponentResolver.class);

        assertInstanceOf(
                JavaSerializationFlightManagementPersistenceManager.class,
                manager.createComponent(properties, resolver));
    }
}
