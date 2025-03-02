package ro.eduardismund.flightmgmt.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The {@code Seat} class represents a seat entity in the flight management system. This class
 * implement {@link Serializable} to allow the seat's state to be persisted and retrieved from
 * storage. It contains information about the row, the seat's name, and whether it is a business
 * seat or not.
 */
@AllArgsConstructor
@Data
@SuppressWarnings("PMD.ShortClassName")
public class Seat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int row;
    private String seatName;
    private boolean businessClass;
}
