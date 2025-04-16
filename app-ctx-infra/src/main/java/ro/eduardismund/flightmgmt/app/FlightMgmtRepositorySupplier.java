package ro.eduardismund.flightmgmt.app;

import java.nio.file.Path;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;

/**
 * Supplier that provides an implementation for the {@link FlightManagementRepository} interface
 * according to configuration properties.
 */
@RequiredArgsConstructor
public class FlightMgmtRepositorySupplier implements Supplier<FlightManagementRepository> {
    public static final String PROP_KEY_REPOSITORY = "repository";
    public static final String PROP_VALUE_REPO_JDBC = "jdbc";
    private final ApplicationContext applicationContext;

    /**
     * Creates an instance of type {@link FlightManagementRepository}, either as {@link JdbcFlightManagementRepository}
     * or as {@link InmemFlightManagementRepository}.
     *
     * @return an instance of an implementation of {@link FlightManagementRepository}
     */
    @Override
    public FlightManagementRepository get() {
        final var env = applicationContext.getEnvironment();
        if (PROP_VALUE_REPO_JDBC.equals(env.getProperty(PROP_KEY_REPOSITORY, PROP_VALUE_REPO_JDBC))) {
            return new JdbcFlightManagementRepository(applicationContext.getBean(DataSource.class));
        } else {
            return new InmemFlightManagementRepository(new JavaSerializationFlightManagementPersistenceManager(
                    Path.of(env.getProperty("filePath", "database.dat"))));
        }
    }
}
