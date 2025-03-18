package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.*;

class ScheduledFlightsServletTest {
    public static final String FLIGHT_NUMBER = "F123";
    public static final String DATE = "2022-12-12";
    private XmlManager xmlManager;
    private FlightManagementService service;
    private ScheduledFlightsServlet subject;
    private DomainMapper domainMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        xmlManager = mock(XmlManager.class);
        domainMapper = mock(DomainMapper.class);
        service = mock(FlightManagementService.class);

        subject = new ScheduledFlightsServlet(xmlManager, service, domainMapper);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void doGet_findScheduledFlight(boolean isPresent) throws IOException {
        final var captor = ArgumentCaptor.forClass(FindScheduledFlightResponse.class);
        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        doReturn(FLIGHT_NUMBER).when(request).getParameter("flight-id");
        doReturn(DATE).when(request).getParameter("departure-date");

        if (isPresent) {
            final var flight = new Flight(FLIGHT_NUMBER);
            final var scheduledFlight = new ScheduledFlight();
            scheduledFlight.setFlight(flight);
            scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-12-12T00:00:00"));
            final var flightItem = new FlightItem();
            flightItem.setNumber(FLIGHT_NUMBER);
            final var scheduledFlightItem = new ScheduledFlightItem();
            scheduledFlightItem.setFlight(flightItem);
            scheduledFlightItem.setDepartureTime(DATE);
            doReturn(scheduledFlightItem).when(domainMapper).mapToScheduledFlightItem(scheduledFlight);
            doReturn(Optional.of(scheduledFlight))
                    .when(service)
                    .findScheduledFlight(FLIGHT_NUMBER, LocalDate.parse(DATE));
        } else {
            doReturn(Optional.empty()).when(service).findScheduledFlight(FLIGHT_NUMBER, LocalDate.parse(DATE));
        }
        subject.doGet(request, response);

        if (isPresent) {
            assertEquals(
                    FLIGHT_NUMBER,
                    captor.getValue().getScheduledFlightItem().getFlight().getNumber());
            assertEquals(DATE, captor.getValue().getScheduledFlightItem().getDepartureTime());
            assertTrue(captor.getValue().isFound());
        } else {
            verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
            assertNull(captor.getValue().getScheduledFlightItem());
            assertFalse(captor.getValue().isFound());
        }
        verify(response).setContentType("text/xml");
    }

    @Test
    void doGet_ScheduledFlightsList() throws IOException {
        final var flight = new Flight(FLIGHT_NUMBER);
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-12-12T00:00:00"));
        final var scheduledFlights = List.of(scheduledFlight);
        final var captor = ArgumentCaptor.forClass(GetScheduledFlightsResponse.class);
        final var flightItem = new FlightItem();
        flightItem.setNumber(FLIGHT_NUMBER);

        final var scheduledFlightItem = new ScheduledFlightItem();
        scheduledFlightItem.setFlight(flightItem);
        scheduledFlightItem.setDepartureTime(DATE);
        doReturn(scheduledFlightItem).when(domainMapper).mapToScheduledFlightItem(scheduledFlight);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doReturn(scheduledFlights).when(service).getScheduledFlights();
        doReturn(scheduledFlightItem).when(domainMapper).mapToScheduledFlightItem(scheduledFlight);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));

        subject.doGet(request, response);

        assertSame(scheduledFlightItem, captor.getValue().getScheduledFlights().getFirst());
        assertTrue(captor.getValue().isSuccess());
    }

    @ParameterizedTest
    @CsvSource({
        "success",
        "ArrivalBeforeDepartureException",
        "AirplaneAlreadyScheduledException",
        "ScheduledFlightAlreadyExistsException",
        "InternalError"
    })
    void doPost(String type)
            throws IOException, ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var command = mock(CreateScheduledFlightCommand.class);
        final var flight = new Flight(FLIGHT_NUMBER);
        final var captor = ArgumentCaptor.forClass(CreateScheduledFlightResponse.class);
        final var reader = mock(BufferedReader.class);
        final var writer = mock(PrintWriter.class);
        doReturn(command).when(xmlManager).unmarshal(reader);

        doReturn(reader).when(request).getReader();
        doReturn(writer).when(response).getWriter();

        doReturn(FLIGHT_NUMBER).when(command).getFlightId();
        doReturn("A123").when(command).getAirplane();
        doReturn(DATE).when(command).getArrival();
        doReturn(DATE).when(command).getDeparture();

        final var airplane = new Airplane("A123");
        doReturn(Optional.of(airplane)).when(service).findAirplane("A123");
        doReturn(Optional.of(flight)).when(service).findFlight("F123");

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-12-12T00:00:00"));

        doReturn(scheduledFlight).when(domainMapper).mapFromCreateScheduledFlightCommand(command, flight, airplane);

        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        switch (type) {
            case "success" -> doNothing().when(service).createScheduledFlight(scheduledFlight);
            case "ScheduledFlightAlreadyExistsException" -> doThrow(ScheduledFlightAlreadyExistsException.class)
                    .when(service)
                    .createScheduledFlight(scheduledFlight);
            case "ArrivalBeforeDepartureException" -> doThrow(ArrivalBeforeDepartureException.class)
                    .when(service)
                    .createScheduledFlight(scheduledFlight);
            case "AirplaneAlreadyScheduledException" -> doThrow(AirplaneAlreadyScheduledException.class)
                    .when(service)
                    .createScheduledFlight(scheduledFlight);
            case "InternalError" -> doThrow(RuntimeException.class)
                    .when(service)
                    .createScheduledFlight(scheduledFlight);
        }
        subject.doPost(request, response);

        switch (type) {
            case "success" -> assertTrue(captor.getValue().isSuccess());
            case "ScheduledFlightAlreadyExistsException" -> {
                verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assertEquals(
                        CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException,
                        captor.getValue().getError());
            }
            case "ArrivalBeforeDepartureException" -> {
                verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assertEquals(
                        CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException,
                        captor.getValue().getError());
            }
            case "AirplaneAlreadyScheduledException" -> {
                verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assertEquals(
                        CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException,
                        captor.getValue().getError());
            }
            case "InternalError" -> {
                verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                assertEquals(
                        CreateScheduledFlightResponse.CsfrErrorType.InternalError,
                        captor.getValue().getError());
            }
        }
        verify(response).setContentType("text/xml");
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertEquals(flight.getNumber(), captor.getValue().getFlightId());
        assertEquals(airplane.getIdNumber(), captor.getValue().getAirplaneId());
        assertEquals(DATE, captor.getValue().getDepartureTime());
        assertEquals(DATE, captor.getValue().getArrivalTime());
    }
}
