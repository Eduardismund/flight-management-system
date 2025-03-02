package ro.eduardismund.flightmgmt.service;

import java.io.Serial;
import java.time.LocalTime;

/**
 * The {@code AirplaneAlreadyScheduledException} class is a custom exception that is thrown when
 * attempting to schedule an airplane to the flight management system that is already in use.
 */
public class AirplaneAlreadyScheduledException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code AirplaneAlreadyScheduledException} with a message indicating the
     * airplane ID is already associated to a scheduled flight on a specified date.
     *
     * @param idNumber the ID of the airplane that caused the conflict
     * @param departureTime the starting time from which the airplane is scheduled for a flight
     * @param arrivalTime the ending time from which the airplane is scheduled for a flight
     */
    public AirplaneAlreadyScheduledException(String idNumber, LocalTime departureTime, LocalTime arrivalTime) {
        super("The airplane with ID "
                + idNumber
                + " is already scheduled between "
                + departureTime
                + " and "
                + arrivalTime);
    }
}
