package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;

@SuppressWarnings("checkstyle:MethodName")
class InmemFlightManagementFlightManagementRepositoryTest {

    private static final String CORRECT_AIRPLANE_NUMBER = "A123";
    private static final String CORRECT_FLIGHT_NUMBER = "ED0001";
    private static final String INCORRECT_AIRPLANE_NUMBER = "A122";
    private static final String INCORRECT_FLIGHT_NUMBER = "ED0002";

    private final InmemFlightManagementPersistenceManager persistenceManager =
            mock(InmemFlightManagementPersistenceManager.class);
    private final InmemFlightManagementRepository repository = new InmemFlightManagementRepository(persistenceManager);

    @Test
    public void init_should_load_persistenceManager_with_correct_data() {
        List<Airplane> airplanes = List.of(new Airplane(CORRECT_AIRPLANE_NUMBER));
        List<Flight> flights = List.of(new Flight(CORRECT_FLIGHT_NUMBER));
        List<ScheduledFlight> scheduledFlights = List.of(new ScheduledFlight());
        List<Booking> bookings = List.of(new Booking());

        InmemFlightManagementRepository repository = new InmemFlightManagementRepository(persistenceManager);

        repository.setAirplanes(airplanes);
        repository.setFlights(flights);
        repository.setScheduledFlights(scheduledFlights);
        repository.setBookings(bookings);

        repository.init();

        ArgumentCaptor<InmemFlightManagementPersistenceManager.Objects> captor =
                forClass(InmemFlightManagementPersistenceManager.Objects.class);
        verify(persistenceManager, times(1)).load(captor.capture());

        InmemFlightManagementPersistenceManager.Objects capturedObjects = captor.getValue();

        List<Airplane> capturedAirplanes = capturedObjects.airplanes();
        assertEquals(airplanes, capturedAirplanes);

        List<Flight> capturedFlights = capturedObjects.flights();
        assertEquals(flights, capturedFlights);

        List<ScheduledFlight> capturedScheduledFlights = capturedObjects.scheduledFlights();
        assertEquals(scheduledFlights, capturedScheduledFlights);

        List<Booking> capturedBookings = capturedObjects.bookings();
        assertEquals(bookings, capturedBookings);
    }

    @Test
    public void addFlight() {
        int flights = repository.getFlights().size();
        final var newFlight = new Flight(CORRECT_FLIGHT_NUMBER);
        repository.addFlight(newFlight);
        assertEquals(flights + 1, repository.getFlights().size());

        ArgumentCaptor<InmemFlightManagementPersistenceManager.Objects> captor =
                ArgumentCaptor.forClass(InmemFlightManagementPersistenceManager.Objects.class);
        verify(persistenceManager).dump(captor.capture());

        InmemFlightManagementPersistenceManager.Objects capturedObjects = captor.getValue();
        assertTrue(capturedObjects.flights().contains(newFlight));
    }

    @Test
    public void findFlight_found_flight() {
        final var expectedFlight = new Flight(CORRECT_FLIGHT_NUMBER);
        repository.addFlight(expectedFlight);
        final var actualFlight = repository.findFlight(CORRECT_FLIGHT_NUMBER);
        assertTrue(actualFlight.isPresent());
        assertEquals(expectedFlight, actualFlight.get());
    }

    @Test
    public void findFlight_not_found_flight() {
        final var expectedFlight = new Flight(INCORRECT_FLIGHT_NUMBER);
        repository.addFlight(expectedFlight);
        final var foundFlight = repository.findFlight(CORRECT_FLIGHT_NUMBER);
        assertFalse(foundFlight.isPresent());
    }

    @Test
    public void addScheduledFlight() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));

        int scheduledFlights = repository.getFlights().size();
        repository.addScheduledFlight(scheduledFlight);

        assertEquals(scheduledFlights + 1, repository.getScheduledFlights().size());

        ArgumentCaptor<InmemFlightManagementPersistenceManager.Objects> captor =
                ArgumentCaptor.forClass(InmemFlightManagementPersistenceManager.Objects.class);
        verify(persistenceManager).dump(captor.capture());

        InmemFlightManagementPersistenceManager.Objects capturedObjects = captor.getValue();
        assertTrue(capturedObjects.scheduledFlights().contains(scheduledFlight));
    }

    @Test
    public void addAirplane() {
        int airplanes = repository.getAirplanes().size();
        final var newAirplane = new Airplane(CORRECT_AIRPLANE_NUMBER);
        repository.addAirplane(newAirplane);
        assertEquals(airplanes + 1, repository.getAirplanes().size());

        ArgumentCaptor<InmemFlightManagementPersistenceManager.Objects> captor =
                ArgumentCaptor.forClass(InmemFlightManagementPersistenceManager.Objects.class);
        verify(persistenceManager).dump(captor.capture());

        InmemFlightManagementPersistenceManager.Objects capturedObjects = captor.getValue();
        assertTrue(capturedObjects.airplanes().contains(newAirplane));
    }

    @Test
    public void findAirplane_found_airplane() {
        final var airplane = new Airplane(CORRECT_AIRPLANE_NUMBER);
        repository.addAirplane(airplane);
        final var actualAirplane = repository.findAirplane(CORRECT_AIRPLANE_NUMBER);
        assertTrue(actualAirplane.isPresent());
        assertEquals(airplane, actualAirplane.get());
    }

    @Test
    public void findAirplane_not_found_airplane() {

        repository.addAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        assertFalse(repository.findAirplane(INCORRECT_AIRPLANE_NUMBER).isPresent());
    }

    @Test
    public void contains_airplane() {
        final var airplane = new Airplane(CORRECT_AIRPLANE_NUMBER);
        repository.setAirplanes(List.of(airplane));
        assertTrue(repository.contains(airplane));
    }

    @Test
    public void doesnt_contain_airplane() {
        final var airplane = new Airplane(CORRECT_AIRPLANE_NUMBER);
        repository.setAirplanes(List.of(airplane));
        final var airplane1 = new Airplane(INCORRECT_AIRPLANE_NUMBER);
        assertFalse(repository.contains(airplane1));
    }

    @Test
    public void contains_flight() {
        final var flight = new Flight(CORRECT_FLIGHT_NUMBER);
        repository.setFlights(List.of(flight));
        assertTrue(repository.contains(flight));
    }

    @Test
    public void doesnt_contain_flight() {
        final var flight = new Flight(CORRECT_FLIGHT_NUMBER);
        repository.setFlights(List.of(flight));
        final var flight1 = new Flight(INCORRECT_FLIGHT_NUMBER);
        assertFalse(repository.contains(flight1));
    }

    @Test
    public void addBooking() {
        final var scheduledFlight = new ScheduledFlight();
        final var passenger = new Passenger("Eduard", "Jitareanu", "11");
        final var booking = new Booking();
        booking.setPassenger(passenger);
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        final var seat = new Seat(11, "A", true);
        booking.setAssignedSeat(seat);
        booking.setScheduledFlight(scheduledFlight);

        int bookings = repository.getBookings().size();
        repository.addBooking(booking);
        assertEquals(bookings + 1, repository.getBookings().size());

        ArgumentCaptor<InmemFlightManagementPersistenceManager.Objects> captor =
                ArgumentCaptor.forClass(InmemFlightManagementPersistenceManager.Objects.class);
        verify(persistenceManager).dump(captor.capture());

        InmemFlightManagementPersistenceManager.Objects capturedObjects = captor.getValue();
        assertTrue(capturedObjects.bookings().contains(booking));
    }

    @Test
    public void findScheduledFlight_with_correct_FlightNumber_and_correct_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);

        final var foundScheduledFlight =
                repository.findScheduledFlight(scheduledFlight.getFlight().getNumber(), date.toLocalDate());

        assertTrue(foundScheduledFlight.isPresent());
    }

    @Test
    public void findScheduledFlight_with_wrong_FlightNumber_and_correct_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);

        final var foundScheduledFlight = repository.findScheduledFlight(INCORRECT_FLIGHT_NUMBER, date.toLocalDate());

        assertFalse(foundScheduledFlight.isPresent());
    }

    @Test
    public void findScheduledFlight_with_correct_FlightNumber_and_wrong_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);

        final var foundScheduledFlight = repository.findScheduledFlight(
                CORRECT_FLIGHT_NUMBER, LocalDate.now().plusMonths(1));

        assertFalse(foundScheduledFlight.isPresent());
    }

    @Test
    public void findScheduledFlight_with_wrong_FlightNumber_and_wrong_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);

        final var foundScheduledFlight = repository.findScheduledFlight(
                INCORRECT_FLIGHT_NUMBER, LocalDate.now().plusMonths(1));

        assertFalse(foundScheduledFlight.isPresent());
    }

    @Test
    public void findScheduledFlight_with_correct_IdNumber_and_correct_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);
        List<ScheduledFlight> expectedFlights = new ArrayList<>();
        expectedFlights.add(scheduledFlight);

        List<ScheduledFlight> flights = repository.findScheduledFlightsForAirplane(
                scheduledFlight.getAirplane().getIdNumber(), date.toLocalDate());

        assertEquals(expectedFlights, flights);
    }

    @Test
    public void findScheduledFlight_with_correct_IdNumber_and_wrong_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date.plusMonths(1));
        repository.addScheduledFlight(scheduledFlight);
        List<ScheduledFlight> expectedFlights = new ArrayList<>();
        expectedFlights.add(scheduledFlight);

        List<ScheduledFlight> flights = repository.findScheduledFlightsForAirplane(
                scheduledFlight.getAirplane().getIdNumber(), date.toLocalDate());

        assertEquals(expectedFlights.size(), flights.size() + 1);
    }

    @Test
    public void findScheduledFlight_with_wrong_IdNumber_and_wrong_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date.plusMonths(1));
        repository.addScheduledFlight(scheduledFlight);
        List<ScheduledFlight> expectedFlights = new ArrayList<>();
        expectedFlights.add(scheduledFlight);

        List<ScheduledFlight> flights =
                repository.findScheduledFlightsForAirplane(INCORRECT_AIRPLANE_NUMBER, date.toLocalDate());

        assertEquals(expectedFlights.size(), flights.size() + 1);
    }

    @Test
    public void findScheduledFlight_with_wrong_IdNumber_and_correct_LocalDate() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(CORRECT_FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(CORRECT_AIRPLANE_NUMBER));
        final var date = LocalDateTime.now();
        scheduledFlight.setDepartureTime(date);
        repository.addScheduledFlight(scheduledFlight);
        List<ScheduledFlight> expectedFlights = new ArrayList<>();
        expectedFlights.add(scheduledFlight);

        List<ScheduledFlight> flights =
                repository.findScheduledFlightsForAirplane(INCORRECT_AIRPLANE_NUMBER, date.toLocalDate());

        assertEquals(expectedFlights.size(), flights.size() + 1);
    }
}
