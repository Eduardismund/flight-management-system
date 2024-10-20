package ro.eduardismund.flightmgmt.service;

import java.io.Serial;

/**
 * The {@code AirplaneAlreadyExistsException} class is a custom exception that is thrown when
 * attempting to add an airplane to the flight management system that already exists.
 */
public class AirplaneAlreadyExistsException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code AirplaneAlreadyExistsException} with a message indicating the airplane
     * ID that already exists.
     *
     * @param airplaneId the ID of the airplane that caused the conflict
     */
    public AirplaneAlreadyExistsException(String airplaneId) {
        super("An Airplane with id " + airplaneId + " already exists");
    }
}
