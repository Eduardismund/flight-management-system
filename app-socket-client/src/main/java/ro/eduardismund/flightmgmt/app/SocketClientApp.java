package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ApplicationContext;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.RemoteFlightManagementService;

/**
 * The {@code SocketClientApp} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.UseUtilityClass"})
public class SocketClientApp {

    /**
     * The entry point of the application It creates an instance of the {@code App} class and starts
     * the execution of the system.
     *
     * @param args command line arguments(not used)
     */
    public static void main(String[] args) {

        final var applicationContext = new ApplicationContext();
        applicationContext.registerComponent(
                ServerConfigProperties.builder().port(6000).build());
        applicationContext.registerComponentClass(AdminCliRunnable.class);
        applicationContext.registerComponentClass(RemoteFlightManagementService.class);
        applicationContext.registerComponentClass(DomainMapper.class);
        applicationContext.registerComponentClass(XmlManager.class);
        applicationContext.registerComponentClass(ClientSocketComponentFactory.class);
        applicationContext.registerComponentClass(AdminUi.class);
        applicationContext.registerComponentClass(CliManagerComponentFactory.class);
        applicationContext.processComponents();
        applicationContext.run(args);
    }
}
