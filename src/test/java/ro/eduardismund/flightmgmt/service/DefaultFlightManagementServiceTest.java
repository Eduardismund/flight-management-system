package ro.eduardismund.flightmgmt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

@SuppressWarnings("checkstyle:MethodName")
class DefaultFlightManagementServiceTest {

    private static final String CORR_AIRPLANE_NUM = "A123";
    private static final String CORR_FLIGHT_NUM = "ED0001";

    private final FlightManagementRepository repo = mock(FlightManagementRepository.class);
    private final DefaultFlightManagementService subject = new DefaultFlightManagementService(repo);

    @Test
    void createAirplane_calls_repo_addAirplane() throws AirplaneAlreadyExistsException {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);

        subject.createAirplane(airplane);

        verify(repo, times(1)).findAirplane(airplane.getIdNumber());
        verify(repo, times(1)).addAirplane(airplane);
    }

    @Test
    void createAirplane_fails_when_airplaneNumber_already_exists() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);

        doReturn(Optional.of(new Airplane(airplane.getIdNumber()))).when(repo).findAirplane(airplane.getIdNumber());

        final var exception =
                assertThrows(AirplaneAlreadyExistsException.class, () -> subject.createAirplane(airplane));

        assertEquals("An Airplane with id " + CORR_AIRPLANE_NUM + " already exists", exception.getMessage());
    }

    @Test
    void createFlight_calls_repo_addFlight() throws FlightAlreadyExistsException {

        final var flight = new Flight(CORR_FLIGHT_NUM);

        subject.createFlight(flight);

        verify(repo, times(1)).findFlight(flight.getNumber());
        verify(repo, times(1)).addFlight(flight);
    }

    @Test
    void createFlight_fails_when_flightNumber_already_in_use() {
        final var flight = new Flight(CORR_FLIGHT_NUM);

        doReturn(Optional.of(new Flight(flight.getNumber()))).when(repo).findFlight(flight.getNumber());

        final var exception = assertThrows(FlightAlreadyExistsException.class, () -> subject.createFlight(flight));

        assertEquals("A flight with number " + CORR_FLIGHT_NUM + " already exists", exception.getMessage());
    }

    @Test
    void createBooking_succeeds_when_calling_repo() {
        final var booking = new Booking();
        subject.createBooking(booking);
        verify(repo, times(1)).addBooking(booking);
    }

    @Test
    void findFlight_calls_repository_found_Flight() {
        doReturn(Optional.of(new Flight(CORR_FLIGHT_NUM))).when(repo).findFlight(CORR_FLIGHT_NUM);
        final var flight = subject.findFlight(CORR_FLIGHT_NUM);
        verify(repo, times(1)).findFlight(CORR_FLIGHT_NUM);
        assertTrue(flight.isPresent());
    }

    @Test
    void findFlight_calls_repository_not_found_Flight() {
        final var flight = subject.findFlight(CORR_FLIGHT_NUM);
        assertTrue(flight.isEmpty());
    }

    @Test
    void findAirplane_calls_repository_found_Airplane() {
        doReturn(Optional.of(new Airplane(CORR_AIRPLANE_NUM))).when(repo).findAirplane(CORR_AIRPLANE_NUM);
        final var airplane = subject.findAirplane(CORR_AIRPLANE_NUM);
        verify(repo, times(1)).findAirplane(CORR_AIRPLANE_NUM);
        assertTrue(airplane.isPresent());
    }

    @Test
    void findAirplane_calls_repository_not_found_Airplane() {
        final var airplane = subject.findAirplane(CORR_AIRPLANE_NUM);
        assertTrue(airplane.isEmpty());
    }

    @Test
    void getFlights_calls_repository_found_flights() {
        doReturn(List.of(new Flight(CORR_FLIGHT_NUM))).when(repo).getFlights();
        final var flights = subject.getFlights();
        verify(repo, times(1)).getFlights();
        assertFalse(flights.isEmpty());
    }

    @Test
    void getFlights_calls_repository_not_found_flights() {
        final var flights = subject.getFlights();
        assertTrue(flights.isEmpty());
    }

    @Test
    void getAirplanes_calls_found_repository() {
        doReturn(List.of(new Airplane(CORR_AIRPLANE_NUM))).when(repo).getAirplanes();
        final var airplanes = subject.getAirplanes();
        verify(repo, times(1)).getAirplanes();
        assertFalse(airplanes.isEmpty());
    }

    @Test
    void getAirplanes_calls_not_found_repository() {
        final var airplanes = subject.getAirplanes();
        assertTrue(airplanes.isEmpty());
    }

    @Test
    void getScheduledFlights_calls_found_repository() {

        final var scheduledFlights = new ScheduledFlight();
        doReturn(List.of(scheduledFlights)).when(repo).getScheduledFlights();
        final var expectedSf = subject.getScheduledFlights();
        verify(repo, times(1)).getScheduledFlights();
        assertFalse(expectedSf.isEmpty());
    }

    // region createScheduledFlight Tests

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void createScheduledFlight_succeeds_when_calling_repo(boolean scheduledInThePast)
            throws ArrivalBeforeDepartureException, AirplaneAlreadyScheduledException,
                    ScheduledFlightAlreadyExistsException {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var departure = LocalDateTime.now();
        final var arrival = LocalDateTime.now().plusHours(11);

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(departure);
        scheduledFlight.setArrivalTime(arrival);

        doReturn(true).when(repo).contains(flight);
        doReturn(true).when(repo).contains(airplane);

        if (scheduledInThePast) {
            final var pastSf = new ScheduledFlight();
            pastSf.setDepartureTime(departure.minusHours(11));
            pastSf.setArrivalTime(arrival.minusHours(11));

            doReturn(List.of(pastSf))
                    .when(repo)
                    .findScheduledFlightsForAirplane(airplane.getIdNumber(), departure.toLocalDate());
        }

        subject.createScheduledFlight(scheduledFlight);

        verify(repo, times(1)).findScheduledFlight(flight.getNumber(), departure.toLocalDate());
        verify(repo, times(1)).findScheduledFlightsForAirplane(airplane.getIdNumber(), departure.toLocalDate());
        verify(repo, times(1)).addScheduledFlight(scheduledFlight);
    }

    @Test
    void createScheduledFlight_fails_when_ScheduledFlight_alreadyExists() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var departure = LocalDateTime.now();
        final var arrival = LocalDateTime.now().plusHours(11);

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(departure);
        scheduledFlight.setArrivalTime(arrival);

        doReturn(true).when(repo).contains(flight);
        doReturn(true).when(repo).contains(airplane);
        doReturn(Optional.of(scheduledFlight))
                .when(repo)
                .findScheduledFlight(flight.getNumber(), departure.toLocalDate());

        final var exception = assertThrows(
                ScheduledFlightAlreadyExistsException.class, () -> subject.createScheduledFlight(scheduledFlight));

        assertEquals(
                "A flight with number " + flight.getNumber() + " already exists on " + departure.toLocalDate(),
                exception.getMessage());
    }

    @Test
    void createScheduledFlight_fails_when_Airplane_in_use() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var departure = LocalDateTime.now();
        final var arrival = LocalDateTime.now().plusHours(11);
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(departure);
        scheduledFlight.setArrivalTime(arrival);

        doReturn(true).when(repo).contains(flight);
        doReturn(true).when(repo).contains(airplane);
        doReturn(List.of(scheduledFlight))
                .when(repo)
                .findScheduledFlightsForAirplane(airplane.getIdNumber(), departure.toLocalDate());

        final var exception = assertThrows(
                AirplaneAlreadyScheduledException.class, () -> subject.createScheduledFlight(scheduledFlight));

        assertEquals(
                "The airplane with ID "
                        + airplane.getIdNumber()
                        + " is already scheduled between "
                        + departure.toLocalTime()
                        + " and "
                        + arrival.toLocalTime(),
                exception.getMessage());
    }

    @Test
    void createScheduledFlight_fails_when_Flight_doesntExist() {
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        doReturn(false).when(repo).contains(flight);

        final var exception =
                assertThrows(IllegalArgumentException.class, () -> subject.createScheduledFlight(scheduledFlight));

        assertEquals("Missing or invalid flight!", exception.getMessage());
    }

    @Test
    void createScheduledFlight_fails_when_Airplane_doesntExist() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);

        doReturn(true).when(repo).contains(flight);
        doReturn(false).when(repo).contains(airplane);

        final var exception =
                assertThrows(IllegalArgumentException.class, () -> subject.createScheduledFlight(scheduledFlight));

        assertEquals("Missing or invalid airplane!", exception.getMessage());
    }

    @Test
    void createScheduledFlight_fails_when_Arrival_before_Departure() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var arrival = LocalDateTime.now();
        final var departure = LocalDateTime.now().plusHours(11);

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(departure);
        scheduledFlight.setArrivalTime(arrival);

        doReturn(true).when(repo).contains(flight);
        doReturn(true).when(repo).contains(airplane);

        final var exception = assertThrows(
                ArrivalBeforeDepartureException.class, () -> subject.createScheduledFlight(scheduledFlight));

        assertEquals(
                "For the given Scheduled Flight, the arrival "
                        + arrival.toLocalTime()
                        + " is before the departure "
                        + departure.toLocalTime(),
                exception.getMessage());
    }
    // endregion createScheduledFlight Tests

    @Test
    void getScheduledFlights_calls_not_found_repository() {
        final var expectedSf = subject.getScheduledFlights();
        assertTrue(expectedSf.isEmpty());
    }

    @Test
    void findScheduledFlight_calls_found_repository() {
        final var airplane = new Airplane(CORR_AIRPLANE_NUM);
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var departure = LocalDateTime.now();
        final var arrival = LocalDateTime.now().plusHours(11);

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setDepartureTime(departure);
        scheduledFlight.setArrivalTime(arrival);

        doReturn(Optional.of(scheduledFlight))
                .when(repo)
                .findScheduledFlight(flight.getNumber(), departure.toLocalDate());

        final var expectedSf = subject.findScheduledFlight(flight.getNumber(), departure.toLocalDate());
        verify(repo, times(1)).findScheduledFlight(flight.getNumber(), departure.toLocalDate());

        assertTrue(expectedSf.isPresent());
    }

    @Test
    void findScheduledFlight_calls_not_found_repository() {
        final var flight = new Flight(CORR_FLIGHT_NUM);
        final var departure = LocalDateTime.now();

        final var expectedSf = subject.findScheduledFlight(flight.getNumber(), departure.toLocalDate());
        verify(repo, times(1)).findScheduledFlight(flight.getNumber(), departure.toLocalDate());

        assertTrue(expectedSf.isEmpty());
    }

    @ParameterizedTest(name = "isOverlapping([{0}, {1}], [{2}, {3}]) expects {4}")
    @CsvSource({
        "2024-09-30T12:00:00,2024-09-30T12:20:00,2024-09-30T12:10:00,2024-09-30T12:30:00,true",
        "2024-09-30T12:00:00,2024-09-30T12:20:00,2024-09-30T12:05:00,2024-09-30T12:10:00,true",
        "2024-09-30T12:00:00,2024-09-30T12:20:00,2024-09-30T12:21:00,2024-09-30T12:30:00,false",
        "2024-09-30T12:00:00,2024-09-30T12:20:00,2024-09-30T12:20:00,2024-09-30T12:30:00,false",
        "2024-09-30T12:00:00,2024-09-30T12:20:00,2024-09-30T12:00:00,2024-09-30T12:20:00,true",
        "2024-09-30T23:50:00,2024-10-01T00:20:00,2024-09-30T12:20:00,2024-09-30T12:30:00,false"
    })
    void isOverlapping(String depart1, String arriv1, String depart2, String arriv2, boolean expected) {
        ScheduledFlight scheduledFlight = new ScheduledFlight();
        ScheduledFlight scheduledFlight2 = new ScheduledFlight();

        scheduledFlight.setDepartureTime(LocalDateTime.parse(depart1));
        scheduledFlight.setArrivalTime(LocalDateTime.parse(arriv1));
        scheduledFlight2.setDepartureTime(LocalDateTime.parse(depart2));
        scheduledFlight2.setArrivalTime(LocalDateTime.parse(arriv2));

        assertEquals(expected, DefaultFlightManagementService.isOverlapping(scheduledFlight, scheduledFlight2));
    }
}
