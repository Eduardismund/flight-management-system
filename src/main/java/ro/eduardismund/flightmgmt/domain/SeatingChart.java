package ro.eduardismund.flightmgmt.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 * The {@code SeatingChart} class represents a seating chart entity in the flight management system.
 * This class implement {@link Serializable} to allow the seating chart's state to be persisted and
 * retrieved from storage. It contains information about the counter of seats, a set of {@link Seat}
 * entities, and a method to initialize the seats.
 */
@Data
@SuppressFBWarnings("EI_EXPOSE_REP")
public class SeatingChart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int NUMBER_OF_BUSINESS_SEATS = 3;
    private int seatsCount;
    private Set<Seat> seats = new HashSet<>();

    /**
     * Constructs a {@code SeatingChart} object with the specified number of rows and seats per row.
     * Initializes the seating chart by calling {@link #standardSeatingChart(int, int)}.
     *
     * @param rowsCount the number of rows in the seating chart
     * @param seatsPerRowCount the number of seats in each row
     */
    public SeatingChart(int rowsCount, int seatsPerRowCount) {
        this.seatsCount = rowsCount * seatsPerRowCount;
        standardSeatingChart(rowsCount, seatsPerRowCount);
    }

    /**
     * Constructs a {@code SeatingChart} object with the specified seats.
     *
     * @param seatsCount the number of seats in the seating chart
     * @param seats a set containing the seats in the seating chart
     */
    public SeatingChart(int seatsCount, Set<Seat> seats) {
        this.seatsCount = seatsCount;
        this.seats = seats;
    }

    /**
     * Initializes the seating chart with the specified number of rows and seats per row. By default,
     * the first three rows are considered business class, while the remaining rows are considered
     * economy class.
     *
     * @param rowsCount the number of rows in the seating chart
     * @param seatsPerRowCount the number of seats in each row
     */
    private void standardSeatingChart(int rowsCount, int seatsPerRowCount) {
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < seatsPerRowCount; j++) {
                Seat seat = new Seat(i + 1, "" + (char) ('A' + j), true);

                if (i >= NUMBER_OF_BUSINESS_SEATS) {
                    seat.setBusinessClass(false);
                }

                seats.add(seat);
            }
        }
    }
}
