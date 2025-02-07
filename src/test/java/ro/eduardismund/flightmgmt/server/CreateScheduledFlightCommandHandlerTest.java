package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyScheduledException;
import ro.eduardismund.flightmgmt.service.ArrivalBeforeDepartureException;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;

class CreateScheduledFlightCommandHandlerTest {
    public static final String F_123 = "F123";
    public static final String A_123 = "A123";
    public static final String DEPARTURE_DATE = "2022-12-12T12:00";
    public static final String ARRIVAL_DATE = "2022-12-12T13:00";
    private CreateScheduledFlightCommandHandler handler;
    private CreateScheduledFlightCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new CreateScheduledFlightCommandHandler();
        command = new CreateScheduledFlightCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand_isSuccessful()
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var scheduledFlight = setupScheduledFlight();

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(scheduledFlight.getFlight().getNumber(), response.getFlightId());
        assertEquals(scheduledFlight.getAirplane().getIdNumber(), response.getAirplaneId());
        assertEquals(scheduledFlight.getArrivalTime().toString(), response.getArrivalTime());
        assertEquals(scheduledFlight.getDepartureTime().toString(), response.getDepartureTime());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertInstanceOf(CreateScheduledFlightResponse.class, response);

        verify(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        verify(service).createScheduledFlight(scheduledFlight);
    }

    @Test
    void handleCommand_ScheduledFlightAlreadyExists()
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var scheduledFlight = setupScheduledFlight();
        doThrow(new ScheduledFlightAlreadyExistsException(F_123, LocalDate.parse("2022-12-12")))
                .when(service)
                .createScheduledFlight(scheduledFlight);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException);

        verify(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        verify(service).createScheduledFlight(scheduledFlight);
    }

    @Test
    void handleCommand_AirplaneAlreadyScheduledException()
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var scheduledFlight = setupScheduledFlight();
        doThrow(new AirplaneAlreadyScheduledException(A_123, LocalTime.parse("12:00"), LocalTime.parse("13:00")))
                .when(service)
                .createScheduledFlight(scheduledFlight);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException);

        verify(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        verify(service).createScheduledFlight(scheduledFlight);
    }

    @Test
    void handleCommand_ArrivalBeforeDepartureException()
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var scheduledFlight = setupScheduledFlight();
        doThrow(new ArrivalBeforeDepartureException(LocalTime.parse("12:00"), LocalTime.parse("13:00")))
                .when(service)
                .createScheduledFlight(scheduledFlight);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException);

        verify(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        verify(service).createScheduledFlight(scheduledFlight);
    }

    @Test
    void handleCommand_InternalError()
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var scheduledFlight = setupScheduledFlight();
        doThrow(new RuntimeException()).when(service).createScheduledFlight(scheduledFlight);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.InternalError);

        verify(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        verify(service).createScheduledFlight(scheduledFlight);
    }

    @Test
    void handleCommand_FlightNotFound() {
        command.setFlightId(F_123);
        handler.handleCommand(command, service, domainMapper);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.InternalError);
        verifyNoInteractions(domainMapper);
    }

    @Test
    void handleCommand_AirplaneNotFound() {
        command.setFlightId(F_123);
        doReturn(Optional.of(new Flight(command.getFlightId()))).when(service).findFlight(command.getFlightId());
        handler.handleCommand(command, service, domainMapper);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertError(response, CreateScheduledFlightResponse.CsfrErrorType.InternalError);
        verifyNoInteractions(domainMapper);
    }

    private static void assertError(
            CreateScheduledFlightResponse response, CreateScheduledFlightResponse.CsfrErrorType internalError) {
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(response.getError(), internalError);
        assertInstanceOf(CreateScheduledFlightResponse.class, response);
    }

    private ScheduledFlight setupScheduledFlight() {
        command.setFlightId(F_123);
        command.setAirplane(A_123);
        final var scheduledFlight = new ScheduledFlight();
        final var flight = new Flight(command.getFlightId());
        doReturn(Optional.of(flight)).when(service).findFlight(flight.getNumber());
        scheduledFlight.setFlight(flight);
        final var airplane = new Airplane(command.getAirplane());
        doReturn(Optional.of(airplane)).when(service).findAirplane(airplane.getIdNumber());
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(LocalDateTime.parse(DEPARTURE_DATE));
        scheduledFlight.setArrivalTime(LocalDateTime.parse(ARRIVAL_DATE));
        doReturn(scheduledFlight)
                .when(domainMapper)
                .mapFromCreateScheduledFlightCommand(
                        command, scheduledFlight.getFlight(), scheduledFlight.getAirplane());
        return scheduledFlight;
    }
}
