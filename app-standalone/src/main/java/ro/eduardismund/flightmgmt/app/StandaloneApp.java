package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ApplicationContext;
import com.github.eduardismund.appctx.Condition;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;
import ro.eduardismund.flightmgmt.service.DefaultFlightManagementService;

/**
 * The {@code App} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.UseUtilityClass"})
public class StandaloneApp {

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
        applicationContext.registerComponentClass(DataSourceComponentFactory.class);
        applicationContext.registerComponentClass(AdminCliRunnable.class);
        applicationContext.registerComponentClass(JdbcFlightManagementRepository.class, useJdbcRepo);
        applicationContext.registerComponentClass(IfmPersistenceManagerComponentFactory.class, useInmemRepo);
        applicationContext.registerComponentClass(InmemFlightManagementRepository.class, useInmemRepo);
        applicationContext.registerComponentClass(DefaultFlightManagementService.class);
        applicationContext.registerComponentClass(AdminUi.class);
        applicationContext.registerComponentClass(CliManagerComponentFactory.class);

        applicationContext.addBeforeRunListener(componentResolver -> componentResolver
                .resolveComponent(FlightManagementRepository.class)
                .init());

        applicationContext.processComponents();

        applicationContext.run(args);
    }
}
