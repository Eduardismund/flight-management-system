package ro.eduardismund.flightmgmt.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * The {@code ScheduledFlight} class represents a scheduled flight entity in the flight management
 * system. This class implement {@link Serializable} to allow the scheduled flight's state to be
 * persisted and retrieved from storage. It contains information about the associated {@link
 * Flight}, {@link Airplane}, the reserved bookings, each {@link Seat} having connected a {@link
 * Booking}, and the departure and arrival dates.
 */
@Data
@SuppressFBWarnings("EI_EXPOSE_REP")
public class ScheduledFlight implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Flight flight;
    private Airplane airplane;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Map<Seat, Booking> bookings = new HashMap<>();

    public List<Seat> getAvailableSeats() {
        return airplane.getSeatingChart().getSeats().stream()
                .filter(seat -> !bookings.containsKey(seat))
                .toList();
    }
}
