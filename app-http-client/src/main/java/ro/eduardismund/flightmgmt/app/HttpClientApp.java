package ro.eduardismund.flightmgmt.app;

import lombok.experimental.UtilityClass;
import org.springframework.context.support.GenericApplicationContext;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.HttpClientFlightMgmtService;

/**
 * Entry point for the Http Client App.
 */
@UtilityClass
public class HttpClientApp {

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
            applicationContext.registerBean(
                    HttpClientFlightMgmtService.class, new HttpClientFlightMgmtServiceSupplier(applicationContext));
            applicationContext.registerBean(DomainMapper.class);
            applicationContext.registerBean(XmlManager.class);
            applicationContext.registerBean(AdminUi.class);
            applicationContext.registerBean(CliManager.class, new CliManagerSupplier());
            applicationContext.refresh();
            applicationContext.getBean(AdminCliRunnable.class).run(args);
        }
    }
}
