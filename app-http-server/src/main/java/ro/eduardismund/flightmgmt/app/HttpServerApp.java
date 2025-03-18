package ro.eduardismund.flightmgmt.app;

import lombok.experimental.UtilityClass;
import ro.eduardismund.appctx.ApplicationContext;
import ro.eduardismund.appctx.Condition;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;
import ro.eduardismund.flightmgmt.server.AirplanesServlet;
import ro.eduardismund.flightmgmt.server.BookingServlet;
import ro.eduardismund.flightmgmt.server.FlightsServlet;
import ro.eduardismund.flightmgmt.server.HttpServer;
import ro.eduardismund.flightmgmt.server.ScheduledFlightsServlet;
import ro.eduardismund.flightmgmt.service.DefaultFlightManagementService;

/**
 *  Entry point for the Http Server App.
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

        final var applicationContext = new ApplicationContext();
        final var useJdbcRepo = Condition.propertyEquals("repository", "jdbc");
        final var useInmemRepo = useJdbcRepo.negate();
        applicationContext.registerComponentClass(DataSourceComponentFactory.class);
        applicationContext.registerComponentClass(HttpServerRunnable.class);
        applicationContext.registerComponentClass(FlightsServlet.class);
        applicationContext.registerComponentClass(AirplanesServlet.class);
        applicationContext.registerComponentClass(ScheduledFlightsServlet.class);
        applicationContext.registerComponentClass(BookingServlet.class);
        applicationContext.registerComponentClass(HttpServer.class);
        applicationContext.registerComponentClass(JdbcFlightManagementRepository.class, useJdbcRepo);
        applicationContext.registerComponentClass(IfmPersistenceManagerComponentFactory.class, useInmemRepo);
        applicationContext.registerComponentClass(InmemFlightManagementRepository.class, useInmemRepo);
        applicationContext.registerComponentClass(DefaultFlightManagementService.class);
        applicationContext.registerComponentClass(DomainMapper.class);
        applicationContext.registerComponentClass(XmlManager.class);

        applicationContext.addBeforeRunListener(componentResolver -> componentResolver
                .resolveComponent(FlightManagementRepository.class)
                .init());

        applicationContext.processComponents();
        applicationContext.run(args);
    }
}
