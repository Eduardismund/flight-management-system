package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ApplicationContext;
import lombok.experimental.UtilityClass;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.HttpClientFlightMgmtService;

/**
 *  Entry point for the Http Client App.
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

        final var applicationContext = new ApplicationContext();

        applicationContext.registerComponentClass(AdminCliRunnable.class);
        applicationContext.registerComponentClass(HttpClientFlightMgmtService.class);
        applicationContext.registerComponentClass(DomainMapper.class);
        applicationContext.registerComponentClass(XmlManager.class);
        applicationContext.registerComponentClass(AdminUi.class);
        applicationContext.registerComponentClass(CliManagerComponentFactory.class);
        applicationContext.processComponents();
        applicationContext.run(args);
    }
}
