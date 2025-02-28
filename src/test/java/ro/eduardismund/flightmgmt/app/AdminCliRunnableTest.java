package ro.eduardismund.flightmgmt.app;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

class AdminCliRunnableTest {

    @Test
    void run() {
        final var repo = mock(FlightManagementRepository.class);
        final var cliManager = mock(CliManager.class);
        final var adminUi = mock(AdminUi.class);

        when(cliManager.readLine())
                .thenReturn("1")
                .thenReturn("2")
                .thenReturn("3")
                .thenReturn("4")
                .thenReturn("6")
                .thenReturn("5");

        final var adminCliRunnable = new AdminCliRunnable(repo, cliManager, adminUi);

        final var args = new String[] {"a"};

        adminCliRunnable.run(args);

        verify(repo).init();
        verify(cliManager).println("Welcome to Flights Management");
        verify(cliManager, times(6))
                .println(
                        """
                            Select option:
                            1. Create Flight
                            2. Create Airplane
                            3. Create Scheduled Flight
                            4. Create Booking
                            5. Exit
                            """);

        verify(adminUi).createFlight();
        verify(adminUi).createAirplane();
        verify(adminUi).createScheduledFlight();
        verify(adminUi).createBooking();
    }
}
