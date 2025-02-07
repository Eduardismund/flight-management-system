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
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Flight;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryFlightTest {
    public static final String FLIGHT_NUMBER = "F123";
    public static final String TEST = "Test";
    public static final String NUMBER = "Number";
    public static final String COMPANY = "Company";
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
        SQLException exception = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(exception, assertThrows(SQLException.class, () -> repository.contains(new Flight(FLIGHT_NUMBER))));

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

        final var exception = new SQLException();
        when(preparedStatement.executeQuery()).thenThrow(exception);

        assertSame(exception, assertThrows(SQLException.class, repository::getFlights));

        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(connection).close();
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

        final var exception = new SQLException();
        doThrow(exception).when(resultSet).getString(NUMBER);

        assertSame(exception, assertThrows(SQLException.class, () -> repository.readFlightDetails(resultSet)));

        verify(resultSet).getString(NUMBER);
        verify(resultSet, never()).getString(COMPANY);
    }
}
