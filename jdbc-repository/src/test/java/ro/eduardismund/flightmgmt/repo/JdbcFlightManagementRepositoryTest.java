package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION", "RV_RETURN_VALUE_IGNORED"})
@ExtendWith(MockitoExtension.class)
class JdbcFlightManagementRepositoryTest {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private JdbcFlightManagementRepository repository;

    @Mock
    private Connection connection;

    @Test
    void init_test() {
        repository.init();
        verifyNoInteractions(connection, dataSource);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Test
    void runInTransaction_rollbackOnException() {
        final var callable = Mockito.mock(JdbcFlightManagementRepository.ConnCallable.class);
        final var exception = new Exception();

        doReturn(connection).when(dataSource).getConnection();
        doThrow(exception).when(callable).call(connection);

        assertSame(exception, assertThrows(Exception.class, () -> repository.runInTransaction(callable)));

        verify(connection).rollback();
        verify(connection, never()).commit();
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
}
