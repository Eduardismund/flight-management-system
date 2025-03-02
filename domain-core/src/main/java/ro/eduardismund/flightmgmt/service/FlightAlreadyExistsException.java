package ro.eduardismund.flightmgmt.service;

import java.io.Serial;

/**
 * The {@code FlightAlreadyExistsException} class is a custom exception that is thrown when an
 * attempt is made to add a flight to the flight management system that already exists.
 */
public class FlightAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code FlightAlreadyExistsException} with a message indicating the flight
     * number that already exists.
     *
     * @param number the flight number that already exists
     */
    public FlightAlreadyExistsException(String number) {
        super("A flight with number " + number + " already exists");
    }
}
