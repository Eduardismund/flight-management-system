package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class FlightsServletTest {
    public static final String FLIGHT_NUMBER = "F123";
    private XmlManager xmlManager;
    private FlightManagementService service;
    private FlightsServlet subject;
    private DomainMapper domainMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        xmlManager = mock(XmlManager.class);
        domainMapper = mock(DomainMapper.class);
        service = mock(FlightManagementService.class);

        subject = new FlightsServlet(xmlManager, service, domainMapper);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void doGet_findFlight(boolean isPresent) throws IOException {
        final var captor = ArgumentCaptor.forClass(FindFlightResponse.class);
        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        doReturn("/flights/" + FLIGHT_NUMBER).when(request).getRequestURI();

        if (isPresent) {
            final var flight = new Flight(FLIGHT_NUMBER);
            final var flightItem = new FlightItem();
            flightItem.setNumber(FLIGHT_NUMBER);
            doReturn(flightItem).when(domainMapper).mapToFlightItem(flight);
            doReturn(Optional.of(flight)).when(service).findFlight(FLIGHT_NUMBER);
        } else {
            doReturn(Optional.empty()).when(service).findFlight(FLIGHT_NUMBER);
        }
        subject.doGet(request, response);

        if (isPresent) {
            assertEquals(FLIGHT_NUMBER, captor.getValue().getFlight().getNumber());
            assertTrue(captor.getValue().isFound());
        } else {
            verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
            assertNull(captor.getValue().getFlight());
            assertFalse(captor.getValue().isFound());
        }
        verify(response).setContentType("text/xml");
    }

    @Test
    void doGet_FlightsList() throws IOException {
        final var flight = new Flight(FLIGHT_NUMBER);
        final var flights = List.of(flight);
        final var captor = ArgumentCaptor.forClass(GetFlightsResponse.class);
        final var flightItem = new FlightItem();
        flightItem.setNumber(FLIGHT_NUMBER);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doReturn("/flights").when(request).getRequestURI();
        doReturn(flights).when(service).getFlights();
        doReturn(flightItem).when(domainMapper).mapToFlightItem(flight);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));

        subject.doGet(request, response);

        assertSame(flightItem, captor.getValue().getFlights().getFirst());
        assertTrue(captor.getValue().isSuccess());
    }

    @ParameterizedTest
    @CsvSource({"success", "FlightAlreadyExists", "InternalError"})
    void doPost(String type) throws IOException, FlightAlreadyExistsException {
        final var command = mock(CreateFlightCommand.class);
        final var flight = new Flight(FLIGHT_NUMBER);
        final var captor = ArgumentCaptor.forClass(CreateFlightResponse.class);
        final var reader = mock(BufferedReader.class);
        final var writer = mock(PrintWriter.class);
        doReturn(reader).when(request).getReader();
        doReturn(writer).when(response).getWriter();

        doReturn(command).when(xmlManager).unmarshal(reader);
        doReturn(FLIGHT_NUMBER).when(command).getNumber();
        doReturn(flight).when(domainMapper).mapFromCreateFlightCommand(command);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        switch (type) {
            case "success" -> doNothing().when(service).createFlight(flight);
            case "FlightAlreadyExists" -> doThrow(FlightAlreadyExistsException.class)
                    .when(service)
                    .createFlight(flight);
            case "InternalError" -> doThrow(RuntimeException.class)
                    .when(service)
                    .createFlight(flight);
        }
        subject.doPost(request, response);

        switch (type) {
            case "success" -> {
                assertTrue(captor.getValue().isSuccess());
                verify(response).setStatus(HttpServletResponse.SC_CREATED);
            }
            case "FlightAlreadyExists" -> {
                verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assertEquals(
                        CreateFlightResponse.CfrErrorType.FlightAlreadyExists,
                        captor.getValue().getError());
            }
            case "InternalError" -> {
                verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                assertEquals(
                        CreateFlightResponse.CfrErrorType.InternalError,
                        captor.getValue().getError());
            }
        }
        verify(response).setContentType("text/xml");
        assertEquals(flight.getNumber(), captor.getValue().getNumber());
    }
}
