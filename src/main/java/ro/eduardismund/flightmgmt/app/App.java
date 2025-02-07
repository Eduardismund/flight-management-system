package ro.eduardismund.flightmgmt.app;

import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;
import ro.eduardismund.flightmgmt.server.Server;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;
import ro.eduardismund.flightmgmt.server.XmlManager;
import ro.eduardismund.flightmgmt.service.DefaultFlightManagementService;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.RemoteFlightManagementService;

/**
 * The {@code App} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.UseUtilityClass"})
public class App {

    /**
     * The entry point of the application It creates an instance of the {@code App} class and starts
     * the execution of the system.
     *
     * @param args command line arguments(not used)
     */
    public static void main(String[] args) {

        final var applicationContext = new ApplicationContext();
        final var useJdbcRepo = Condition.propertyEquals("repository", "jdbc");
        final var useInmemRepo = useJdbcRepo.negate();
        final var serverMode = Condition.propertyEquals("mode", "server");
        final var remoteClientMode = Condition.propertyEquals("clientMode", "remote");
        applicationContext.registerComponentClass(DataSourceComponentFactory.class);
        applicationContext.registerComponentClass(ServerRunnable.class, serverMode);
        applicationContext.registerComponentClass(Server.class, serverMode);
        applicationContext.registerComponent(
                ServerConfigProperties.builder().port(6000).build());
        applicationContext.registerComponentClass(AdminCliRunnable.class, serverMode.negate());
        applicationContext.registerComponentClass(JdbcFlightManagementRepository.class, useJdbcRepo);
        applicationContext.registerComponentClass(IfmPersistenceManagerComponentFactory.class, useInmemRepo);
        applicationContext.registerComponentClass(InmemFlightManagementRepository.class, useInmemRepo);
        applicationContext.registerComponentClass(DefaultFlightManagementService.class, remoteClientMode.negate());
        applicationContext.registerComponentClass(RemoteFlightManagementService.class, remoteClientMode);
        applicationContext.registerComponentClass(DomainMapper.class, remoteClientMode.or(serverMode));
        applicationContext.registerComponentClass(XmlManager.class, remoteClientMode.or(serverMode));
        applicationContext.registerComponentClass(ClientSocketComponentFactory.class, remoteClientMode);
        applicationContext.registerComponentClass(AdminUi.class);
        applicationContext.registerComponentClass(CliManagerComponentFactory.class);
        applicationContext.processComponents();
        applicationContext.run(args);
    }
}
