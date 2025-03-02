package ro.eduardismund.flightmgmt.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * The {@code Booking} class represents a booking entity in the flight management system. This class
 * implement {@link Serializable} to allow the booking's state to be persisted and retrieved from
 * storage. It contains information about the {@link Passenger}, the {@link ScheduledFlight}, and
 * the {@link Seat} assigned to the passenger.
 */
@Data
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Booking implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Passenger passenger;
    private ScheduledFlight scheduledFlight;
    private Seat assignedSeat;
}
