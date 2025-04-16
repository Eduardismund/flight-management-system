package ro.eduardismund.flightmgmt.app;

import javax.sql.DataSource;
import org.springframework.context.support.GenericApplicationContext;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.server.Logger;
import ro.eduardismund.flightmgmt.server.Server;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;
import ro.eduardismund.flightmgmt.service.DefaultFlightManagementService;

/**
 * The {@code App} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.UseUtilityClass"})
public class SocketServerApp {

    /**
     * The entry point of the application It creates an instance of the {@code App} class and starts
     * the execution of the system.
     *
     * @param args command line arguments(not used)
     */
    public static void main(String[] args) {

        try (var applicationContext = new GenericApplicationContext()) {
            applicationContext.setEnvironment(new EnvironmentSupplier().get());
            applicationContext.registerBean(DataSource.class, new DataSourceSupplier(applicationContext));
            applicationContext.registerBean(ServerRunnable.class);
            applicationContext.registerBean(Server.class);
            applicationContext.registerBean(
                    ServerConfigProperties.class, new ServerConfigPropertiesSupplier(applicationContext));
            applicationContext.registerBean(
                    FlightManagementRepository.class, new FlightMgmtRepositorySupplier(applicationContext));
            applicationContext.registerBean(DefaultFlightManagementService.class);
            applicationContext.registerBean(DomainMapper.class);
            applicationContext.registerBean(XmlManager.class);
            applicationContext.registerBean(Logger.class, new LoggerSupplier());
            applicationContext.addApplicationListener(new FlightMgmtRepositoryInitApplicationListener());
            applicationContext.refresh();
            applicationContext.getBean(ServerRunnable.class).run(args);
        }
    }
}
