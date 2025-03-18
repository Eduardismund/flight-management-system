package ro.eduardismund.flightmgmt.server;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.startup.Tomcat;

/**
 * Http Server for flight management REST API.
 */
@RequiredArgsConstructor
public class HttpServer {

    final FlightsServlet flightServlet;
    final AirplanesServlet airplanesServlet;
    final BookingServlet bookingServlet;
    final ScheduledFlightsServlet sfServlet;

    /**
     * Starts a tomcat server and binds the servlet corresponding to each entity.
     */
    @SneakyThrows
    public void start() {
        final var tomcat = createTomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        final var context = tomcat.addContext("", System.getProperty("java.io.tmpdir"));

        tomcat.addServlet("", "FlightsServlet", flightServlet);
        tomcat.addServlet("", "AirplanesServlet", airplanesServlet);
        tomcat.addServlet("", "ScheduledFlightsServlet", sfServlet);
        tomcat.addServlet("", "BookingServlet", bookingServlet);

        context.addServletMappingDecoded("/flights", "FlightsServlet");
        context.addServletMappingDecoded("/flights/*", "FlightsServlet");

        context.addServletMappingDecoded("/airplanes", "AirplanesServlet");
        context.addServletMappingDecoded("/airplanes/*", "AirplanesServlet");

        context.addServletMappingDecoded("/scheduled-flights", "ScheduledFlightsServlet");
        context.addServletMappingDecoded("/scheduled-flights/*", "ScheduledFlightsServlet");

        context.addServletMappingDecoded("/bookings", "BookingServlet");
        context.addServletMappingDecoded("/bookings/*", "BookingServlet");

        tomcat.start();
        tomcat.getServer().await();
    }

    /**
     * Initializes a Tomcat object.
     *
     * @return a Tomcat object
     */
    Tomcat createTomcat() {
        return new Tomcat();
    }
}
