package ro.eduardismund.flightmgmt.app;

import javax.sql.DataSource;
import lombok.experimental.UtilityClass;
import org.springframework.context.support.GenericApplicationContext;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.server.AirplanesServlet;
import ro.eduardismund.flightmgmt.server.BookingServlet;
import ro.eduardismund.flightmgmt.server.FlightsServlet;
import ro.eduardismund.flightmgmt.server.HttpServer;
import ro.eduardismund.flightmgmt.server.HttpServerProperties;
import ro.eduardismund.flightmgmt.server.ScheduledFlightsServlet;
import ro.eduardismund.flightmgmt.service.DefaultFlightManagementService;

/**
 * Entry point for the Http Server App.
 */
@UtilityClass
public class HttpServerApp {

    /**
     * The entry point of the application. It creates an instance of the {@code HttpServerApp} class and starts
     * the execution of the system.
     *
     * @param args command line arguments(not used)
     */
    public static void main(String[] args) {

        try (var applicationContext = new GenericApplicationContext()) {
            applicationContext.setEnvironment(new EnvironmentSupplier().get());
            applicationContext.registerBean(DataSource.class, new DataSourceSupplier(applicationContext));
            applicationContext.registerBean(HttpServerRunnable.class);
            applicationContext.registerBean(FlightsServlet.class);
            applicationContext.registerBean(AirplanesServlet.class);
            applicationContext.registerBean(ScheduledFlightsServlet.class);
            applicationContext.registerBean(BookingServlet.class);
            applicationContext.registerBean(
                    HttpServerProperties.class, new HttpServerPropertiesSupplier(applicationContext));
            applicationContext.registerBean(HttpServer.class);
            applicationContext.registerBean(
                    FlightManagementRepository.class, new FlightMgmtRepositorySupplier(applicationContext));
            applicationContext.registerBean(DefaultFlightManagementService.class);
            applicationContext.registerBean(DomainMapper.class);
            applicationContext.registerBean(XmlManager.class);
            applicationContext.addApplicationListener(new FlightMgmtRepositoryInitApplicationListener());
            applicationContext.refresh();
            applicationContext.getBean(HttpServerRunnable.class).run();
        }
    }
}
