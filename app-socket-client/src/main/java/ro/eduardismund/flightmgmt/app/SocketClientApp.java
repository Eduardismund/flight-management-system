package ro.eduardismund.flightmgmt.app;

import java.net.Socket;
import org.springframework.context.support.GenericApplicationContext;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.cli.CliManager;
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

        try (var applicationContext = new GenericApplicationContext()) {
            applicationContext.setEnvironment(new EnvironmentSupplier().get());
            applicationContext.registerBean(AdminCliRunnable.class);
            applicationContext.registerBean(RemoteFlightManagementService.class);
            applicationContext.registerBean(DomainMapper.class);
            applicationContext.registerBean(XmlManager.class);
            applicationContext.registerBean(Socket.class, new ClientSocketSupplier(applicationContext));
            applicationContext.registerBean(AdminUi.class);
            applicationContext.registerBean(CliManager.class, new CliManagerSupplier());
            applicationContext.refresh();
            applicationContext.getBean(AdminCliRunnable.class).run(args);
        }
    }
}
