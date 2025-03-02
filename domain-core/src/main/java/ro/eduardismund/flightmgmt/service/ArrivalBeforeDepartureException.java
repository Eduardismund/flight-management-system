package ro.eduardismund.flightmgmt.service;

import java.io.Serial;
import java.time.LocalTime;

/**
 * The {@code ArrivalBeforeDepartureException} class is a custom exception that is thrown when an
 * attempt is made to schedule a flight where the arrival time is earlier than the departure time.
 */
public class ArrivalBeforeDepartureException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code ArrivalBeforeDepartureException} with a message indicating that the
     * arrival time is before the departure time.
     *
     * @param departureTime the scheduled departure time of the flight
     * @param arrivalTime the scheduled arrival time of the flight
     */
    public ArrivalBeforeDepartureException(LocalTime departureTime, LocalTime arrivalTime) {
        super("For the given Scheduled Flight, the arrival "
                + arrivalTime
                + " is before the departure "
                + departureTime);
    }
}
