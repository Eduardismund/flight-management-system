package ro.eduardismund.flightmgmt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class ScheduledFlightTest {
    private ScheduledFlight scheduledFlight;
    private Seat seat1;
    private Seat seat2;

    private static final String CORRECT_AIRPLANE_NUM = "A123";
    private static final String CORRECT_FLIGHT_NUM = "ED0001";

    @BeforeEach
    void setUp() {
        final var airplane = new Airplane(CORRECT_AIRPLANE_NUM);
        final var flight = new Flight(CORRECT_FLIGHT_NUM);
        scheduledFlight = new ScheduledFlight();

        seat1 = new Seat(1, "A", true);
        seat2 = new Seat(1, "B", true);

        airplane.setSeatingChart(new SeatingChart(2, 2)); // Example for 2 rows, 2 seats per row
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setFlight(flight);

        scheduledFlight.setDepartureTime(LocalDateTime.now());
        scheduledFlight.setArrivalTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void testGetAvailableSeats_WhenNoBookings_ReturnsAllSeats() {
        scheduledFlight.setBookings(new HashMap<>());

        final var availableSeats = scheduledFlight.getAvailableSeats();

        assertEquals(4, availableSeats.size(), "Expected 4 available seats, but got: " + availableSeats.size());
    }

    @Test
    void testGetAvailableSeats_WhenSomeSeatsBooked_ReturnsAvailableSeats() {
        final var booking1 = new Booking();
        scheduledFlight.getBookings().put(seat1, booking1);

        final var availableSeats = scheduledFlight.getAvailableSeats();

        assertEquals(3, availableSeats.size(), "Expected 3 available seats, but got: " + availableSeats.size());
        assertTrue(availableSeats.contains(seat2), "Seat 2 should be available.");
        assertFalse(availableSeats.contains(seat1), "Seat 1 should not be available.");
    }
}
