package ro.eduardismund.flightmgmt.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The {@code Passenger} class represents a passenger entity in the flight management system. This
 * class implement {@link Serializable} to allow the passenger's state to be persisted and retrieved
 * from storage. It contains information about the passenger's first and last name and their unique
 * ID document.
 */
@AllArgsConstructor
@Data
public class Passenger implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private final String idDocument;
}
