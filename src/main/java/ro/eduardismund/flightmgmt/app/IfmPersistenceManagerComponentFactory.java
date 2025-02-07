package ro.eduardismund.flightmgmt.app;

import java.nio.file.Path;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementPersistenceManager;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;

/**
 * A factory class responsible for creating an instance of {@link InmemFlightManagementPersistenceManager}.
 * It creates a {@link JavaSerializationFlightManagementPersistenceManager} with the file path configured in
 * the environment.
 */
public class IfmPersistenceManagerComponentFactory
        implements ComponentFactory<InmemFlightManagementPersistenceManager> {

    /**
     * Creates an instance of {@link InmemFlightManagementPersistenceManager} using the configured file path.
     *
     * @param properties The environment providing configuration properties.
     * @param resolver The component resolver (not used in this context).
     * @return A new instance of {@link InmemFlightManagementPersistenceManager}.
     */
    @Override
    public InmemFlightManagementPersistenceManager createComponent(Environment properties, ComponentResolver resolver) {
        return new JavaSerializationFlightManagementPersistenceManager(Path.of(properties.getProperty("filePath")));
    }
}
