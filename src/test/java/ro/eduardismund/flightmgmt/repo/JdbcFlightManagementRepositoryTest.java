package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import static org.mockito.Mockito.verifyNoInteractions;
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
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryTest {
    public static final String SQL_WHERE_FLIGHTNUMBER = "WHERE FlightNumber = ? AND CAST(DepartureTime AS DATE) = ?";
    public static final String FLIGHT_NUMBER = "F123";
    public static final String TEST = "Test";
    public static final String NUMBER = "Number";
    public static final String COMPANY = "Company";
    public static final String AIRPLANE_NUMBER = "A123";
    public static final String SCHEDULEDFLIGHT_ID = "F123_2024-12-25";
    public static final String FIRST = "Eduard-David";
    public static final String LAST = "Jitareanu";
    public static final String TEST_1 = "Test1";
    public static final String TEST_2 = "Test2";

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

    @Test
    void init_test() {
        repository.init();
        verifyNoInteractions(connection, dataSource);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void runInTransaction_rollbackOnException() {
        final var callable = mock(JdbcFlightManagementRepository.ConnCallable.class);
        final var ex = new Exception();

        doReturn(connection).when(dataSource).getConnection();
        doThrow(ex).when(callable).call(connection);

        assertSame(ex, assertThrows(Exception.class, () -> repository.runInTransaction(callable)));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findScheduledFlightByWhereClause_returnsFlight() {
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        final var repoSpy = spy(repository);

        final var scheduledFlight = getScheduledFlight();
        doReturn(scheduledFlight).when(repoSpy).readScheduledFlightDetails(connection, resultSet);

        String whereClause = SQL_WHERE_FLIGHTNUMBER;
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
    void addFlight() {
        stabDataSourceAndConnection();

        final var flight = new Flight(FLIGHT_NUMBER);
        flight.setCompany(TEST);
        repository.addFlight(flight);

        verify(connection).prepareStatement("INSERT INTO Flight(Number, Company) VALUES (?,?)");

        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).setString(2, TEST);
        verify(preparedStatement).close();

        verify(preparedStatement).executeUpdate();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findFlight_returnsFlight() {
        stabDataSourceAndConnection();
        doReturn(resultSet).when(preparedStatement).executeQuery();
        doReturn(true).when(resultSet).next();
        doReturn(FLIGHT_NUMBER).when(resultSet).getString(NUMBER);
        doReturn(TEST).when(resultSet).getString(COMPANY);

        final var flight = repository.findFlight(FLIGHT_NUMBER);

        assertTrue(flight.isPresent());

        assertEquals(FLIGHT_NUMBER, flight.get().getNumber());
        assertEquals(TEST, flight.get().getCompany());

        verify(connection).prepareStatement("SELECT Number, Company  FROM Flight WHERE Number = ?");
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findFlight_returnsEmptyFlight() {
        stabDataSourceAndConnection();
        doReturn(resultSet).when(preparedStatement).executeQuery();
        doReturn(false).when(resultSet).next();

        final var flight = repository.findFlight(FLIGHT_NUMBER);

        assertFalse(flight.isPresent());

        verify(connection).prepareStatement("SELECT Number, Company  FROM Flight WHERE Number = ?");
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findFlight_whenQueryFails_throwsException() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> repository.findFlight(FLIGHT_NUMBER));

        verify(connection).prepareStatement("SELECT Number, Company  FROM Flight WHERE Number = ?");
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findFlight_whenConnectionIsNull_throwsException() {
        assertThrows(NullPointerException.class, () -> repository.findFlight(null, FLIGHT_NUMBER));
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
        final var expectedScheduledFlightOptional = Optional.of(new ScheduledFlight());

        when(dataSource.getConnection()).thenReturn(connection);

        final var psParamsSetterArgumentCaptor =
                ArgumentCaptor.forClass(JdbcFlightManagementRepository.PsParamsSetter.class);
        doReturn(expectedScheduledFlightOptional)
                .when(spyRepo)
                .findScheduledFlightByWhereClause(
                        same(connection), eq(SQL_WHERE_FLIGHTNUMBER), psParamsSetterArgumentCaptor.capture());

        final var scheduledFlight = spyRepo.findScheduledFlight(FLIGHT_NUMBER, LocalDate.of(2024, 12, 25));

        assertSame(expectedScheduledFlightOptional, scheduledFlight);

        verify(connection).close();

        psParamsSetterArgumentCaptor.getValue().setParams(preparedStatement);
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
        final var expectedScheduledFlightOptional = Optional.empty();

        when(dataSource.getConnection()).thenReturn(connection);

        final var psParamsSetterArgumentCaptor =
                ArgumentCaptor.forClass(JdbcFlightManagementRepository.PsParamsSetter.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final var scheduledFlight = spyRepo.findScheduledFlight("F999", LocalDate.of(2024, 12, 25));

        assertSame(expectedScheduledFlightOptional, scheduledFlight);

        verify(connection).close();

        verify(spyRepo)
                .findScheduledFlightByWhereClause(
                        same(connection), eq(SQL_WHERE_FLIGHTNUMBER), psParamsSetterArgumentCaptor.capture());

        psParamsSetterArgumentCaptor.getValue().setParams(preparedStatement);

        verify(preparedStatement, times(2)).setString(1, "F999");
        verify(preparedStatement, times(2)).setDate(2, Date.valueOf(LocalDate.of(2024, 12, 25)));

        verify(preparedStatement).executeQuery();

        verify(resultSet).next();
    }

    @SneakyThrows
    @Test
    void getBookingsOfScheduledFlight_throwsException() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenThrow(new SQLException());

        final var sf = getScheduledFlight();

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
        final var ex = new SQLException();

        doThrow(ex).when(resultSet).getString("FirstName");

        assertSame(ex, assertThrows(SQLException.class, () -> repository.readBookingDetails(resultSet)));
    }

    @SneakyThrows
    @Test
    void findScheduledFlight_throwsSqlException() {
        final var spyRepo = spy(repository);

        doReturn(connection).when(dataSource).getConnection();
        final var ex = new IllegalStateException();

        doThrow(ex)
                .when(spyRepo)
                .findScheduledFlightByWhereClause(
                        same(connection),
                        eq(SQL_WHERE_FLIGHTNUMBER),
                        any(JdbcFlightManagementRepository.PsParamsSetter.class));

        assertSame(
                ex,
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
    void addAirplane_insertsAirplaneAndSeatsIntoDatabase() {
        stabDataSourceAndConnection();
        final var seat = new Seat(1, "A", true);
        final var seatingChart = new SeatingChart(1, Set.of(seat));
        final var airplane = new Airplane(AIRPLANE_NUMBER);
        airplane.setSeatingChart(seatingChart);
        airplane.setModel(TEST);

        repository.addAirplane(airplane);

        verify(connection).prepareStatement("INSERT INTO Airplane(IdNumber, Model,SeatsCount) VALUES(?,?,?)");
        verify(preparedStatement, times(2)).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).setString(2, TEST);
        verify(preparedStatement).setInt(3, 1);

        verify(connection)
                .prepareStatement("INSERT INTO Seat(AirplaneIdNumber, Row,SeatName,BusinessClass) VALUES(?,?,?,?)");
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).setString(3, "A");
        verify(preparedStatement).setBoolean(4, true);
        verify(preparedStatement, times(2)).executeUpdate();
        verify(preparedStatement, times(2)).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void runInTransaction_commitsOnSuccess() {
        when(dataSource.getConnection()).thenReturn(connection);

        final var expectedResult = "Success";

        final var result = repository.runInTransaction(conn -> {
            verify(connection).setAutoCommit(false);
            return expectedResult;
        });

        assertEquals(expectedResult, result);
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).close();
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"true", "false"})
    void getConnection_setsCorrectProperties(boolean readOnly) {
        when(dataSource.getConnection()).thenReturn(connection);

        @SuppressWarnings("PMD.CloseResource")
        Connection result = repository.getConnection(readOnly);

        verify(connection).setAutoCommit(false);
        verify(connection).setReadOnly(readOnly);

        assertSame(connection, result);
    }

    @SneakyThrows
    @Test
    void runInTransaction_rollbacksOnException() {
        when(dataSource.getConnection()).thenReturn(connection);

        final var exception = new RuntimeException("Test exception");
        assertThrows(
                RuntimeException.class,
                () -> repository.runInTransaction(conn -> {
                    verify(connection).setAutoCommit(false);
                    throw exception;
                }));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void runInTransaction_throwsSqlExceptionOnCommitFailure() {
        when(dataSource.getConnection()).thenReturn(connection);

        doThrow(new SQLException("Commit failed")).when(connection).commit();

        assertThrows(
                SQLException.class,
                () -> repository.runInTransaction(conn -> {
                    verify(connection).setAutoCommit(false);
                    return "not here";
                }));

        verify(connection).rollback();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getSeatingChart_returnsSet() {
        final var spyRepo = spy(repository);

        final var seat1 = new Seat(1, "A", true);
        final var seat2 = new Seat(2, "B", false);
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(
                        """
                SELECT s.Row, s.SeatName, s.BusinessClass
                FROM Seat s
                WHERE s.AirplaneIdNumber = ?""");
        doReturn(resultSet).when(preparedStatement).executeQuery();

        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        when(resultSet.getInt("Row")).thenReturn(1).thenReturn(2);
        when(resultSet.getString("SeatName")).thenReturn("A").thenReturn("B");
        when(resultSet.getBoolean("BusinessClass")).thenReturn(true).thenReturn(false);

        final var seats = spyRepo.getSeatingChart(connection, AIRPLANE_NUMBER);

        assertEquals(2, seats.size());
        assertTrue(seats.contains(seat1));
        assertTrue(seats.contains(seat2));

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
        verify(preparedStatement).close();
    }

    @SneakyThrows
    @Test
    void contains_returnsTrueIfAirplaneExists() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        assertTrue(repository.contains(new Airplane(AIRPLANE_NUMBER)));
        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(resultSet).next();
        verify(resultSet).getInt(1);
    }

    @SneakyThrows
    @Test
    void contains_returnsFalseIfAirplaneDoesNotExist() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        assertFalse(repository.contains(new Airplane(AIRPLANE_NUMBER)));
        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(resultSet).next();
        verify(resultSet).getInt(1);
    }

    @SneakyThrows
    @Test
    void contains_returnsFalseIfResultSetIsEmpty() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertFalse(repository.contains(new Airplane(AIRPLANE_NUMBER)));
        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(resultSet).next();
        verify(resultSet, never()).getInt(1);
    }

    @SneakyThrows
    @Test
    void contains_throwsSqlExceptionIfAirplane() {
        stabDataSourceAndConnection();
        final var ex = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, () -> repository.contains(new Airplane(AIRPLANE_NUMBER))));
        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void contains_resultSetThrowsException() {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        final var ex = new SQLException();
        when(resultSet.next()).thenReturn(false);

        doThrow(ex).when(resultSet).close();

        assertSame(ex, assertThrows(SQLException.class, () -> repository.contains(new Airplane(AIRPLANE_NUMBER))));
    }

    @Test
    void contains_returnsTrueIfFlightExists() throws SQLException {
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        assertTrue(repository.contains(new Flight(FLIGHT_NUMBER)));
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
    }

    @SneakyThrows
    @Test
    void contains_returnsFalseWhenNoRowsReturned() {

        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act & Assert
        assertFalse(repository.contains(new Flight(FLIGHT_NUMBER)));
        verify(preparedStatement).setString(1, FLIGHT_NUMBER); // Verify parameter binding
        verify(preparedStatement).executeQuery(); // Verify query execution
        verify(resultSet).close(); // Ensure resources are closed
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void contains_returnsFalseIfFlightDoesNotExist() {
        // Arrange
        stabDataSourceAndConnection();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        assertFalse(repository.contains(new Flight(FLIGHT_NUMBER)));
        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(resultSet).close();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void contains_throwsSqlException() {
        stabDataSourceAndConnection();
        SQLException ex = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, () -> repository.contains(new Flight(FLIGHT_NUMBER))));

        verify(preparedStatement).setString(1, FLIGHT_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getFlights_returnsFlights() {
        stabDataSourceAndConnection();
        final var spyRepo = spy(repository);

        final var flight1 = new Flight(FLIGHT_NUMBER);
        flight1.setCompany(TEST_1);
        final var flight2 = new Flight("F124");
        flight2.setCompany(TEST_2);

        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        doReturn(flight1).doReturn(flight2).when(spyRepo).readFlightDetails(resultSet);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        List<Flight> flights = spyRepo.getFlights();

        assertEquals(2, flights.size());
        assertEquals(FLIGHT_NUMBER, flights.get(0).getNumber());
        assertEquals(TEST_1, flights.get(0).getCompany());
        assertEquals("F124", flights.get(1).getNumber());
        assertEquals(TEST_2, flights.get(1).getCompany());

        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getFlights_throwsSqlException() {
        stabDataSourceAndConnection();

        final var ex = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, repository::getFlights));

        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getAirplanes_returnsAirplanes() {
        stabDataSourceAndConnection();
        final var spyRepo = spy(repository);

        final var air1 = new Airplane(AIRPLANE_NUMBER);
        air1.setModel(TEST_1);
        air1.setSeatingChart(new SeatingChart(1, 1));
        final var air2 = new Airplane("A124");
        air2.setModel(TEST_2);
        air2.setSeatingChart(new SeatingChart(1, 1));

        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        doReturn(air1).doReturn(air2).when(spyRepo).readAirplaneDetails(connection, resultSet);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        final var airplanes = spyRepo.getAirplanes();

        assertEquals(2, airplanes.size());
        assertEquals(AIRPLANE_NUMBER, airplanes.getFirst().getIdNumber());
        assertEquals(TEST_1, airplanes.getFirst().getModel());
        assertEquals(1, airplanes.getFirst().getSeatingChart().getSeatsCount());
        assertEquals(
                Set.of(new Seat(1, "A", true)),
                airplanes.getFirst().getSeatingChart().getSeats());

        assertEquals("A124", airplanes.getLast().getIdNumber());
        assertEquals(TEST_2, airplanes.getLast().getModel());
        assertEquals(1, airplanes.getLast().getSeatingChart().getSeatsCount());
        assertEquals(
                Set.of(new Seat(1, "A", true)),
                airplanes.getLast().getSeatingChart().getSeats());

        verify(preparedStatement).executeQuery();
        verify(resultSet, times(3)).next();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void getAirplanes_throwsSqlException() {
        stabDataSourceAndConnection();

        final var ex = new SQLException();

        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, repository::getAirplanes));

        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void readAirplaneDetails_test() {
        final var repoSpy = spy(repository);

        doReturn(AIRPLANE_NUMBER).when(resultSet).getString("IdNumber");
        doReturn(TEST_1).when(resultSet).getString("Model");
        doReturn(1).when(resultSet).getInt("SeatsCount");
        doReturn(Set.of(new Seat(1, "A", true))).when(repoSpy).getSeatingChart(connection, AIRPLANE_NUMBER);

        final var airplane = repoSpy.readAirplaneDetails(connection, resultSet);

        assertNotNull(airplane);

        assertEquals(AIRPLANE_NUMBER, airplane.getIdNumber());
        assertEquals(TEST_1, airplane.getModel());
        assertEquals(1, airplane.getSeatingChart().getSeatsCount());

        verify(resultSet).getString("IdNumber");
        verify(resultSet).getString("Model");
        verify(resultSet).getInt("SeatsCount");

        verify(repoSpy).getSeatingChart(connection, AIRPLANE_NUMBER);
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
        final var ex = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, repository::getScheduledFlights));

        verify(preparedStatement).close();
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findAirplane_returnsAirplaneWhenExists() {
        final var repoSpy = spy(repository);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        final var airplaneId = AIRPLANE_NUMBER;
        final var airplane = new Airplane(AIRPLANE_NUMBER);
        airplane.setModel("Boeing 1");
        airplane.setSeatingChart(new SeatingChart(1, Set.of(new Seat(1, "A", true))));

        when(resultSet.next()).thenReturn(true).thenReturn(false);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        doReturn(airplane).when(repoSpy).readAirplaneDetails(connection, resultSet);

        final var foundAirplane = repoSpy.findAirplane(connection, airplaneId);

        assertTrue(foundAirplane.isPresent());
        assertEquals(airplaneId, foundAirplane.get().getIdNumber());
        assertEquals("Boeing 1", foundAirplane.get().getModel());
        assertEquals(1, foundAirplane.get().getSeatingChart().getSeatsCount());

        verify(preparedStatement).setString(1, airplaneId);
        verify(preparedStatement).executeQuery();
    }

    @SneakyThrows
    @Test
    void findAirplane_returnsAirplaneWhenDoesntExist() {
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);

        when(resultSet.next()).thenReturn(false);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        final var foundAirplane = repository.findAirplane(connection, "A999");

        assertFalse(foundAirplane.isPresent());

        verify(preparedStatement).setString(1, "A999");
        verify(preparedStatement).executeQuery();
        verify(resultSet).next();
    }

    @SneakyThrows
    @Test
    void findAirplane_whenConnectionFails_throwsException() {
        final var se = new SQLException();
        when(dataSource.getConnection()).thenThrow(se);

        final var repoSpy = spy(repository);

        assertSame(se, assertThrows(SQLException.class, () -> repoSpy.findAirplane(AIRPLANE_NUMBER)));
    }

    @SneakyThrows
    @Test
    void findAirplane_returnAirplaneWhenExists() {
        when(dataSource.getConnection()).thenReturn(connection);

        final var repoSpy = spy(repository);

        final var airplane = new Airplane(AIRPLANE_NUMBER);
        airplane.setModel(TEST);
        airplane.setSeatingChart(new SeatingChart(1, Set.of(new Seat(1, "A", true))));

        doReturn(Optional.of(airplane)).when(repoSpy).findAirplane(connection, AIRPLANE_NUMBER);

        final var resultedAirplane = repoSpy.findAirplane(AIRPLANE_NUMBER);

        assertTrue(resultedAirplane.isPresent());

        assertEquals(AIRPLANE_NUMBER, resultedAirplane.get().getIdNumber());
        assertEquals(TEST, resultedAirplane.get().getModel());
        assertEquals(1, resultedAirplane.get().getSeatingChart().getSeatsCount());

        verify(repoSpy).findAirplane(connection, AIRPLANE_NUMBER);
        verify(connection).close();
    }

    @SneakyThrows
    @Test
    void findAirplane_throwsSqlException() {
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);
        SQLException ex = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(ex);

        assertSame(ex, assertThrows(SQLException.class, () -> repository.findAirplane(connection, AIRPLANE_NUMBER)));

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
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

    @SneakyThrows
    @Test
    void readFlightDetails_readsData() {
        final var repoSpy = spy(repository);

        doReturn(FLIGHT_NUMBER).when(resultSet).getString(NUMBER);
        doReturn(TEST_1).when(resultSet).getString(COMPANY);

        final var flight = repoSpy.readFlightDetails(resultSet);

        assertNotNull(flight);

        assertEquals(FLIGHT_NUMBER, flight.getNumber());
        assertEquals(TEST_1, flight.getCompany());

        verify(resultSet).getString(NUMBER);
        verify(resultSet).getString(COMPANY);
    }

    @SneakyThrows
    @Test
    void readFlightDetails_throwsSqlException() {

        final var ex = new SQLException();
        doThrow(ex).when(resultSet).getString(NUMBER);

        assertSame(ex, assertThrows(SQLException.class, () -> repository.readFlightDetails(resultSet)));

        verify(resultSet).getString(NUMBER);
        verify(resultSet, never()).getString(COMPANY);
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

        final var sf = repoSpy.readScheduledFlightDetails(connection, resultSet);

        assertNotNull(sf);

        assertEquals(FLIGHT_NUMBER, sf.getFlight().getNumber());
        assertEquals(AIRPLANE_NUMBER, sf.getAirplane().getIdNumber());
        assertEquals(LocalDateTime.of(2024, 12, 25, 12, 0), sf.getDepartureTime());
        assertEquals(LocalDateTime.of(2024, 12, 25, 10, 0), sf.getArrivalTime());

        assertEquals(1, sf.getBookings().size());
        assertSame(booking, sf.getBookings().values().iterator().next());
        assertSame(sf, booking.getScheduledFlight());

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
        final var ex = new SQLException();
        doThrow(ex).when(resultSet).getString("AirplaneIdNumber");

        assertSame(
                ex,
                assertThrows(SQLException.class, () -> repository.readScheduledFlightDetails(connection, resultSet)));
    }
}
