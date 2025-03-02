package ro.eduardismund.flightmgmt.service;

import java.io.Serial;
import java.time.LocalDate;

/**
 * The {@code ScheduledFlightAlreadyExistsException} class is a custom exception that is thrown when
 * an attempt is made to add a scheduled flight to the flight management system that conflicts with
 * an existing scheduled flight.
 */
public class ScheduledFlightAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code ScheduledFlightAlreadyExistsException} with a message indicating the
     * flight number and date that caused the conflict.
     *
     * @param idNumber the flight number that already exists
     * @param localDate the date on which the flight number already exists
     */
    public ScheduledFlightAlreadyExistsException(String idNumber, LocalDate localDate) {
        super("A flight with number " + idNumber + " already exists on " + localDate);
    }
}
