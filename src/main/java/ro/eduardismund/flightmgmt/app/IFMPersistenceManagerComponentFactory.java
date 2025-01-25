package ro.eduardismund.flightmgmt.app;

import java.nio.file.Path;
import java.util.Properties;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementPersistenceManager;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;

public class IFMPersistenceManagerComponentFactory
        implements ComponentFactory<InmemFlightManagementPersistenceManager> {
    @Override
    public InmemFlightManagementPersistenceManager createComponent(Properties properties, ComponentResolver resolver) {
        return new JavaSerializationFlightManagementPersistenceManager(Path.of(properties.getProperty("filePath")));
    }
}
