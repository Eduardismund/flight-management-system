package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;

class FlightMgmtRepositorySupplierTest {
    private FlightMgmtRepositorySupplier subject;
    private ApplicationContext mockAppContext;

    @BeforeEach
    void setUp() {
        mockAppContext = mock(ApplicationContext.class);
        subject = new FlightMgmtRepositorySupplier(mockAppContext);
    }

    @Test
    void get_returnsJdbcRepositoryInstance() {

        final var mockDataSource = mock(DataSource.class);
        doReturn(mockDataSource).when(mockAppContext).getBean(DataSource.class);
        final var mockEnvironment = mock(Environment.class);
        doReturn("jdbc").when(mockEnvironment).getProperty("repository", "jdbc");
        doReturn(mockEnvironment).when(mockAppContext).getEnvironment();

        final var repository = subject.get();

        assertNotNull(repository);
        assertInstanceOf(JdbcFlightManagementRepository.class, repository);
    }

    @Test
    void get_returnsInmemRepositoryInstance() {

        final var mockEnvironment = mock(Environment.class);
        doReturn("inmem").when(mockEnvironment).getProperty("repository", "jdbc");
        doReturn("databaseTest").when(mockEnvironment).getProperty("filePath", "database.dat");
        doReturn(mockEnvironment).when(mockAppContext).getEnvironment();

        final var repository = subject.get();

        assertNotNull(repository);
        assertInstanceOf(InmemFlightManagementRepository.class, repository);
    }
}
