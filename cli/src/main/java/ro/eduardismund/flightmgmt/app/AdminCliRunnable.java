package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ApplicationRunnable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.cli.CliManager;

/**
 * CLI-based Flight Management application that allows the user to manage flights,
 * airplanes, scheduled flights, and bookings through a simple menu-driven interface.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class AdminCliRunnable implements ApplicationRunnable {
    private final CliManager cliManager;
    private final AdminUi adminUi;

    /**
     *Executes the Flight Management application, initializing necessary components
     * and provides a CLI for managing flights, airplanes, scheduled flights and bookings.
     *
     * @param args CLI arguments
     */
    @Override
    public void run(String[] args) {
        cliManager.println("Welcome to Flights Management");
        boolean notFinished = true;
        while (notFinished) {
            cliManager.println(
                    """
                            Select option:
                            1. Create Flight
                            2. Create Airplane
                            3. Create Scheduled Flight
                            4. Create Booking
                            5. Exit
                            """);
            final var option = cliManager.readLine();
            switch (option) {
                case "1":
                    adminUi.createFlight();
                    break;
                case "2":
                    adminUi.createAirplane();
                    break;
                case "3":
                    adminUi.createScheduledFlight();
                    break;
                case "4":
                    adminUi.createBooking();
                    break;
                case "5":
                    notFinished = false;
                    break;
                default:
            }
        }
    }
}
