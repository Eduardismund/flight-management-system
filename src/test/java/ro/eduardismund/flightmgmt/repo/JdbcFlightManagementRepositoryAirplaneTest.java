package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryAirplaneTest {
    public static final String TEST = "Test";
    public static final String AIRPLANE_NUMBER = "A123";
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
        final var exception = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(
                exception, assertThrows(SQLException.class, () -> repository.contains(new Airplane(AIRPLANE_NUMBER))));
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
        final var exception = new SQLException();
        when(resultSet.next()).thenReturn(false);

        doThrow(exception).when(resultSet).close();

        assertSame(
                exception, assertThrows(SQLException.class, () -> repository.contains(new Airplane(AIRPLANE_NUMBER))));
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

        final var exception = new SQLException();

        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(exception, assertThrows(SQLException.class, repository::getAirplanes));

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
        final var exception = new SQLException();
        when(dataSource.getConnection()).thenThrow(exception);

        final var repoSpy = spy(repository);

        assertSame(exception, assertThrows(SQLException.class, () -> repoSpy.findAirplane(AIRPLANE_NUMBER)));
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
        SQLException exception = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(
                exception,
                assertThrows(SQLException.class, () -> repository.findAirplane(connection, AIRPLANE_NUMBER)));

        verify(preparedStatement).setString(1, AIRPLANE_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
    }
}
