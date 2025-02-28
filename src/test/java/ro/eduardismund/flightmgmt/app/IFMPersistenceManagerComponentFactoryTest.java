package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;

class IFMPersistenceManagerComponentFactoryTest {

    @Test
    void createComponent() {
        final var properties = mock(Properties.class);
        doReturn("test").when(properties).getProperty("filePath");

        final var manager = new IFMPersistenceManagerComponentFactory();
        final var resolver = mock(ComponentResolver.class);

        assertInstanceOf(
                JavaSerializationFlightManagementPersistenceManager.class,
                manager.createComponent(properties, resolver));
    }
}
