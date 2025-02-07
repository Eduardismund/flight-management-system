package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryScheduledFlightTest {
    public static final String SQL_WHERE_FLIGHTNUM = "WHERE FlightNumber = ? AND CAST(DepartureTime AS DATE) = ?";
    public static final String FLIGHT_NUMBER = "F123";
    public static final String AIRPLANE_NUMBER = "A123";
    public static final String SCHEDULEDFLIGHT_ID = "F123_2024-12-25";

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
    @Test
    void findScheduledFlightByWhereClause_returnsFlight() {
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        final var repoSpy = spy(repository);

        final var scheduledFlight = getScheduledFlight();
        doReturn(scheduledFlight).when(repoSpy).readScheduledFlightDetails(connection, resultSet);

        String whereClause = SQL_WHERE_FLIGHTNUM;
        final var psParamsSetter = mock(JdbcFlightManagementRepository.PsParamsSetter.class);
        doReturn(resultSet).when(preparedStatement).executeQuery();
        doReturn(true).when(resultSet).next();

        final var result = repoSpy.findScheduledFlightByWhereClause(connection, whereClause, psParamsSetter);

        assertTrue(result.isPresent());
        assertSame(scheduledFlight, result.get());

        verify(connection)
                .prepareStatement(
                        """
                        SELECT AirplaneIdNumber, FlightNumber, ArrivalTime, DepartureTime
                        FROM ScheduledFlight
                        """
                                + whereClause);

        verify(psParamsSetter).setParams(preparedStatement);
    }

    @SneakyThrows
    @Test
    void findScheduledFlightsForAirplane_isEmpty() {
        stabDataSourceAndConnection();
        final var repoSpy = spy(repository);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final var scheduledFlights =
                repoSpy.findScheduledFlightsForAirplane(AIRPLANE_NUMBER, LocalDate.of(2024, 12, 25));

        assertTrue(scheduledFlights.isEmpty());

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
        verify(repoSpy, never()).readScheduledFlightDetails(any(), any());
    }

    @SneakyThrows
    @Test
    void findScheduledFlightsForAirplane_returnsScheduledFlight() {
        stabDataSourceAndConnection();
        final var repoSpy = spy(repository);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doReturn(getScheduledFlight()).when(repoSpy).readScheduledFlightDetails(connection, resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false);

        final var scheduledFlights =
                repoSpy.findScheduledFlightsForAirplane(AIRPLANE_NUMBER, LocalDate.of(2024, 12, 25));

        assertEquals(1, scheduledFlights.size());
        ScheduledFlight flight = scheduledFlights.getFirst();
        assertEquals(FLIGHT_NUMBER, flight.getFlight().getNumber());
        assertEquals(AIRPLANE_NUMBER, flight.getAirplane().getIdNumber());
        assertEquals(LocalDateTime.of(2024, 12, 25, 10, 0), flight.getDepartureTime());
        assertEquals(LocalDateTime.of(2024, 12, 25, 12, 0), flight.getArrivalTime());

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findScheduledFlightsForAirplane_queryFails_throwsException() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());

        assertThrows(
                SQLException.class,
                () -> repository.findScheduledFlightsForAirplane(AIRPLANE_NUMBER, LocalDate.of(2024, 12, 25)));

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void addScheduledFlight() {
        stabDataSourceAndConnection();

        final var scheduledFlight = spy(getScheduledFlight());
        final var booking = new Booking();
        final var spyRepo = spy(repository);

        doNothing().when(spyRepo).addBooking(any());
        doReturn(Map.of(new Seat(1, "A", true), booking)).when(scheduledFlight).getBookings();
        spyRepo.addScheduledFlight(scheduledFlight);

        verify(preparedStatement).setString(1, SCHEDULEDFLIGHT_ID);
        verify(preparedStatement).setString(2, AIRPLANE_NUMBER);
        verify(preparedStatement).setString(3, FLIGHT_NUMBER);
        verify(preparedStatement).setTimestamp(4, Timestamp.valueOf(scheduledFlight.getDepartureTime()));
        verify(preparedStatement).setTimestamp(5, Timestamp.valueOf(scheduledFlight.getArrivalTime()));

        verify(spyRepo, times(1)).addBooking(booking);
        verify(preparedStatement).executeUpdate();
        verify(connection).close();
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
    void findScheduledFlight_returnsScheduledFlight() {
        final var spyRepo = spy(repository);
        final var expectedSfOptional = Optional.of(new ScheduledFlight());

        when(dataSource.getConnection()).thenReturn(connection);

        final var psParamsSetter = ArgumentCaptor.forClass(JdbcFlightManagementRepository.PsParamsSetter.class);
        doReturn(expectedSfOptional)
                .when(spyRepo)
                .findScheduledFlightByWhereClause(same(connection), eq(SQL_WHERE_FLIGHTNUM), psParamsSetter.capture());

        final var scheduledFlight = spyRepo.findScheduledFlight(FLIGHT_NUMBER, LocalDate.of(2024, 12, 25));

        assertSame(expectedSfOptional, scheduledFlight);

        verify(connection).close();

        psParamsSetter.getValue().setParams(preparedStatement);
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));
    }

    @SneakyThrows
    @Test
    void findScheduledFlight_whenSqlExceptionOccurs_throwsSqlException() {
        final var spyRepo = spy(repository);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> spyRepo.findScheduledFlight(FLIGHT_NUMBER, LocalDate.of(2024, 12, 25)));

        verify(connection).prepareStatement(anyString());
    }

    @SneakyThrows
    @Test
    void findScheduledFlight_whenNoResultsFound_returnsEmpty() {
        final var spyRepo = spy(repository);
        final var expectedSfOptional = Optional.empty();

        when(dataSource.getConnection()).thenReturn(connection);

        final var psParamsSetter = ArgumentCaptor.forClass(JdbcFlightManagementRepository.PsParamsSetter.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final var scheduledFlight = spyRepo.findScheduledFlight("F999", LocalDate.of(2024, 12, 25));

        assertSame(expectedSfOptional, scheduledFlight);

        verify(connection).close();

        verify(spyRepo)
                .findScheduledFlightByWhereClause(same(connection), eq(SQL_WHERE_FLIGHTNUM), psParamsSetter.capture());

        psParamsSetter.getValue().setParams(preparedStatement);

        verify(preparedStatement, times(2)).setString(1, "F999");
        verify(preparedStatement, times(2)).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));

        verify(preparedStatement).executeQuery();

        verify(resultSet).next();
    }

    @SneakyThrows
    @Test
    void findScheduledFlight_throwsSqlException() {
        final var spyRepo = spy(repository);

        doReturn(connection).when(dataSource).getConnection();
        final var exception = new IllegalStateException();

        doThrow(exception)
                .when(spyRepo)
                .findScheduledFlightByWhereClause(
                        same(connection),
                        eq(SQL_WHERE_FLIGHTNUM),
                        any(JdbcFlightManagementRepository.PsParamsSetter.class));

        assertSame(
                exception,
                assertThrows(
                        IllegalStateException.class,
                        () -> spyRepo.findScheduledFlight(FLIGHT_NUMBER, LocalDate.of(2024, 12, 25))));

        verify(connection).close();
    }

    private void setupResultSetScheduledFlights() throws SQLException {
        when(resultSet.getString("AirplaneIdNumber")).thenReturn(AIRPLANE_NUMBER);
        when(resultSet.getString("Id")).thenReturn(SCHEDULEDFLIGHT_ID);
        when(resultSet.getString("FlightNumber")).thenReturn(FLIGHT_NUMBER);
        when(resultSet.getTimestamp("ArrivalTime"))
                .thenReturn(Timestamp.valueOf(LocalDateTime.of(2024, 12, 25, 10, 0)));
        when(resultSet.getTimestamp("DepartureTime"))
                .thenReturn(Timestamp.valueOf(LocalDateTime.of(2024, 12, 25, 12, 0)));
    }

    @SneakyThrows
    @Test
    void getScheduledFlights_returnsScheduledFlights() {

        stabDataSourceAndConnection();
        final var spyRepo = spy(repository);

        final var scheduledFlight1 = getScheduledFlight();
        final var scheduledFlight2 = new ScheduledFlight();
        scheduledFlight2.setFlight(new Flight("F124"));
        scheduledFlight2.setAirplane(new Airplane("A124"));
        scheduledFlight2.setDepartureTime(LocalDateTime.of(2024, 12, 26, 15, 0));
        scheduledFlight2.setArrivalTime(LocalDateTime.of(2024, 12, 26, 17, 0));
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        doReturn(true, true, false).when(resultSet).next();

        doReturn(scheduledFlight1)
                .doReturn(scheduledFlight2)
                .when(spyRepo)
                .readScheduledFlightDetails(connection, resultSet);

        final var scheduledFlights = spyRepo.getScheduledFlights();

        assertEquals(2, scheduledFlights.size());
        assertTrue(scheduledFlights.contains(scheduledFlight1));
        assertTrue(scheduledFlights.contains(scheduledFlight2));

        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getScheduledFlights_throwsSqlException() {

        stabDataSourceAndConnection();
        final var exception = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(exception, assertThrows(SQLException.class, repository::getScheduledFlights));

        verify(preparedStatement).close();
        verify(connection).close();
    }

    @Test
    void readScheduledFlightDetails_readsData() throws SQLException {
        setupResultSetScheduledFlights();
        final var flight1 = new Flight(FLIGHT_NUMBER);
        final var airplane1 = new Airplane(AIRPLANE_NUMBER);
        final var repoSpy = spy(repository);

        doReturn(Optional.of(flight1)).when(repoSpy).findFlight(connection, FLIGHT_NUMBER);
        doReturn(Optional.of(airplane1)).when(repoSpy).findAirplane(connection, AIRPLANE_NUMBER);
        final var booking = new Booking();
        doReturn(List.of(booking)).when(repoSpy).getBookingsOfScheduledFlight(eq(SCHEDULEDFLIGHT_ID));

        final var scheduledFlight = repoSpy.readScheduledFlightDetails(connection, resultSet);

        assertNotNull(scheduledFlight);

        assertEquals(FLIGHT_NUMBER, scheduledFlight.getFlight().getNumber());
        assertEquals(AIRPLANE_NUMBER, scheduledFlight.getAirplane().getIdNumber());
        assertEquals(LocalDateTime.of(2024, 12, 25, 12, 0), scheduledFlight.getDepartureTime());
        assertEquals(LocalDateTime.of(2024, 12, 25, 10, 0), scheduledFlight.getArrivalTime());

        assertEquals(1, scheduledFlight.getBookings().size());
        assertSame(booking, scheduledFlight.getBookings().values().iterator().next());
        assertSame(scheduledFlight, booking.getScheduledFlight());

        verify(resultSet).getString("AirplaneIdNumber");
        verify(resultSet).getString("Id");
        verify(resultSet).getString("FlightNumber");
        verify(resultSet).getTimestamp("ArrivalTime");
        verify(resultSet).getTimestamp("DepartureTime");

        verify(repoSpy).findFlight(connection, FLIGHT_NUMBER);
        verify(repoSpy).findAirplane(connection, AIRPLANE_NUMBER);
        verify(repoSpy).getBookingsOfScheduledFlight(SCHEDULEDFLIGHT_ID);
    }

    @SneakyThrows
    @Test
    void readScheduledFlightDetails_throwsSqlException() {
        final var exception = new SQLException();
        doThrow(exception).when(resultSet).getString("AirplaneIdNumber");

        assertSame(
                exception,
                assertThrows(SQLException.class, () -> repository.readScheduledFlightDetails(connection, resultSet)));
    }
}
