package ro.eduardismund.flightmgmt.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MethodName")
class SeatingChartTest {
    private static final int ROWS_COUNT = 5;
    private static final int SEATS_PER_ROW = 4;
    private SeatingChart seatingChart;

    @BeforeEach
    void setUp() {
        seatingChart = new SeatingChart(ROWS_COUNT, SEATS_PER_ROW);
    }

    @Test
    void seatingChart_initializedCorrectly() {
        int expectedSeatsCount = ROWS_COUNT * SEATS_PER_ROW;
        assertEquals(expectedSeatsCount, seatingChart.getSeatsCount());

        Set<Seat> seats = seatingChart.getSeats();
        assertEquals(expectedSeatsCount, seats.size());

        final int[] businessClass = {0};
        final int[] economyClass = {0};
        seatingChart.getSeats().forEach(seat -> {
            if (seat.isBusinessClass()) {
                businessClass[0]++;
            } else {
                economyClass[0]++;
            }
        });

        assertEquals(3 * SEATS_PER_ROW, businessClass[0]);
        assertEquals((ROWS_COUNT - 3) * SEATS_PER_ROW, economyClass[0]);
    }

    @Test
    void seatingChart_hasCorrectRowAndSeatLabels() {
        Set<Seat> seats = seatingChart.getSeats();
        for (Seat seat : seats) {
            int row = seat.getRow();
            String seatLabel = seat.getSeatName();
            assertTrue(row >= 1 && row <= ROWS_COUNT);
            assertTrue(seatLabel.charAt(0) >= 'A' && seatLabel.charAt(0) < ('A' + SEATS_PER_ROW));
        }
    }

    @Test
    void setSeatingChart_constructor() {
        final var seats = Set.<Seat>of();
        final var seatingChart = new SeatingChart(1, seats);
        assertEquals(1, seatingChart.getSeatsCount());
        assertSame(seats, seatingChart.getSeats());
    }
}
