package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpServerTest {
    FlightsServlet flightServlet;
    AirplanesServlet airplanesServlet;
    BookingServlet bookingServlet;
    ScheduledFlightsServlet sfServlet;
    HttpServer subject;

    @BeforeEach
    void setUp() {
        flightServlet = mock(FlightsServlet.class);
        airplanesServlet = mock(AirplanesServlet.class);
        bookingServlet = mock(BookingServlet.class);
        sfServlet = mock(ScheduledFlightsServlet.class);
        subject = spy(new HttpServer(flightServlet, airplanesServlet, bookingServlet, sfServlet));
    }

    @Test
    void start_throwsLifecycleException() throws LifecycleException {
        final var mockTomcat = mock(Tomcat.class);
        final var lifecycleException = new LifecycleException();
        final var mockContext = mock(Context.class);

        doReturn(mockContext).when(mockTomcat).addContext(anyString(), anyString());
        doThrow(lifecycleException).when(mockTomcat).start();
        doReturn(mockTomcat).when(subject).createTomcat();

        assertSame(lifecycleException, assertThrows(LifecycleException.class, subject::start));
    }

    @Test
    void start() throws LifecycleException {

        final var mockTomcat = mock(Tomcat.class);
        final var mockContext = mock(Context.class);
        final var mockServer = mock(Server.class);

        doReturn(mockTomcat).when(subject).createTomcat();
        doReturn(mockContext).when(mockTomcat).addContext(anyString(), anyString());
        doReturn(mockServer).when(mockTomcat).getServer();

        subject.start();

        verify(mockTomcat).setPort(8080);
        verify(mockTomcat).getConnector();
        verify(mockTomcat).addContext("", System.getProperty("java.io.tmpdir"));
        verify(mockTomcat).addServlet("", "FlightsServlet", flightServlet);
        verify(mockTomcat).addServlet("", "AirplanesServlet", airplanesServlet);
        verify(mockTomcat).addServlet("", "ScheduledFlightsServlet", sfServlet);
        verify(mockTomcat).addServlet("", "BookingServlet", bookingServlet);

        verify(mockContext).addServletMappingDecoded("/flights", "FlightsServlet");
        verify(mockContext).addServletMappingDecoded("/flights/*", "FlightsServlet");

        verify(mockContext).addServletMappingDecoded("/airplanes", "AirplanesServlet");
        verify(mockContext).addServletMappingDecoded("/airplanes/*", "AirplanesServlet");

        verify(mockContext).addServletMappingDecoded("/scheduled-flights", "ScheduledFlightsServlet");
        verify(mockContext).addServletMappingDecoded("/scheduled-flights/*", "ScheduledFlightsServlet");

        verify(mockContext).addServletMappingDecoded("/bookings", "BookingServlet");
        verify(mockContext).addServletMappingDecoded("/bookings/*", "BookingServlet");

        verify(mockTomcat).start();
        verify(mockServer).await();
    }

    @Test
    void createTomcat() {
        assertInstanceOf(Tomcat.class, new HttpServer(null, null, null, null).createTomcat());
    }
}
