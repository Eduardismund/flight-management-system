package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryBookingTest {
    public static final String FLIGHT_NUMBER = "F123";
    public static final String AIRPLANE_NUMBER = "A123";
    public static final String SCHEDULEDFLIGHT_ID = "F123_2024-12-25";
    public static final String FIRST = "Eduard-David";
    public static final String LAST = "Jitareanu";

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JdbcFlightManagementRepository repository;

    @Mock
    private Connection connection;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PreparedStatement preparedStatement;

    @SneakyThrows
    void stabDataSourceAndConnection() {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
    }

    @SneakyThrows
    private static ScheduledFlight getScheduledFlight() {

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(FLIGHT_NUMBER));
        scheduledFlight.setAirplane(new Airplane(AIRPLANE_NUMBER));
        scheduledFlight.setDepartureTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        scheduledFlight.setArrivalTime(LocalDateTime.of(2024, 12, 25, 12, 0));
        return scheduledFlight;
    }

    @SneakyThrows
    @Test
    void getBookingsOfScheduledFlight_throwsException() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> repository.getBookingsOfScheduledFlight(FLIGHT_NUMBER));

        verify(connection)
                .prepareStatement(
                        """
                     SELECT FirstName, LastName, IdDocument, ScheduledFlightId, SeatRow, SeatName, BusinessClass
                     FROM SeatBooking
                     WHERE ScheduledFlightId = ?""");
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void readBookingDetails_throwsException() {
        final var exception = new SQLException();

        doThrow(exception).when(resultSet).getString("FirstName");

        assertSame(exception, assertThrows(SQLException.class, () -> repository.readBookingDetails(resultSet)));
    }

    @SneakyThrows
    @Test
    void addBooking() {
        stabDataSourceAndConnection();
        final var person = new Passenger(FIRST, LAST, "1234");
        final var scheduledFlight = getScheduledFlight();
        final var booking = new Booking();

        booking.setPassenger(person);
        booking.setScheduledFlight(scheduledFlight);
        booking.setAssignedSeat(new Seat(1, "A", true));

        repository.addBooking(booking);

        verify(connection)
                .prepareStatement(
                        """
                INSERT INTO SeatBooking(FirstName, LastName, IdDocument,
                ScheduledFlightId, SeatRow, SeatName, BusinessClass)
                VALUES(?,?,?,?,?,?,?)""");

        verify(preparedStatement).setString(1, FIRST);
        verify(preparedStatement).setString(2, LAST);
        verify(preparedStatement).setString(3, "1234");
        verify(preparedStatement).setString(4, SCHEDULEDFLIGHT_ID);
        verify(preparedStatement).setInt(5, 1);
        verify(preparedStatement).setString(6, "A");
        verify(preparedStatement).setBoolean(7, true);
        verify(preparedStatement, times(1)).close();

        verify(preparedStatement).executeUpdate();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getBookingsOfScheduledFlight_returnsBookings() {
        stabDataSourceAndConnection();
        final var spyRepo = spy(repository);

        final var booking1 = new Booking();
        final var booking2 = new Booking();
        booking1.setPassenger(new Passenger(FIRST, LAST, "111"));
        booking1.setAssignedSeat(new Seat(1, "A", true));
        booking2.setPassenger(new Passenger("John", "Doe", "112"));
        booking2.setAssignedSeat(new Seat(2, "B", false));

        doReturn(resultSet).when(preparedStatement).executeQuery();
        doReturn(true, true, false).when(resultSet).next();

        doReturn(booking1, booking2).when(spyRepo).readBookingDetails(resultSet);

        final var bookings = spyRepo.getBookingsOfScheduledFlight(SCHEDULEDFLIGHT_ID);

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));

        assertNull(booking1.getScheduledFlight());
        assertNull(booking2.getScheduledFlight());

        verify(preparedStatement).setString(1, SCHEDULEDFLIGHT_ID);
        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void readBookingDetails_test() {
        final var repoSpy = spy(repository);

        doReturn(FIRST).when(resultSet).getString("FirstName");
        doReturn(LAST).when(resultSet).getString("LastName");
        doReturn("111").when(resultSet).getString("IdDocument");
        doReturn(1).when(resultSet).getInt("SeatRow");
        doReturn("A").when(resultSet).getString("SeatName");
        doReturn(true).when(resultSet).getBoolean("BusinessClass");

        final var booking = repoSpy.readBookingDetails(resultSet);

        assertNotNull(booking);

        assertEquals(FIRST, booking.getPassenger().getFirstName());
        assertEquals(LAST, booking.getPassenger().getLastName());
        assertEquals("111", booking.getPassenger().getIdDocument());
        assertEquals(1, booking.getAssignedSeat().getRow());
        assertEquals("A", booking.getAssignedSeat().getSeatName());
        assertTrue(booking.getAssignedSeat().isBusinessClass());

        verify(resultSet).getString("FirstName");
        verify(resultSet).getString("LastName");
        verify(resultSet).getString("IdDocument");
        verify(resultSet).getInt("SeatRow");
        verify(resultSet).getString("SeatName");
        verify(resultSet).getBoolean("BusinessClass");
    }
}
