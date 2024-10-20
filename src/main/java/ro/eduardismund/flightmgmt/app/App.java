package ro.eduardismund.flightmgmt.app;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.repo.InmemFlightManagementRepository;
import ro.eduardismund.flightmgmt.repo.JavaSerializationFlightManagementPersistenceManager;
import ro.eduardismund.flightmgmt.service.Service;

/**
 * The {@code App} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
public class App {

    InmemFlightManagementRepository repo = new InmemFlightManagementRepository(
            new JavaSerializationFlightManagementPersistenceManager(Path.of("database.dat")));
    Service service = new Service(repo);
    AdminUi adminUi = new AdminUi(service);
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    /**
     * It initializes the repository and provides a menu for the user to create and manage flights,
     * airplanes, etc.
     */
    public void execute() {
        repo.init();
        System.out.println("Welcome to Flights Management");
        while (true) {
            System.out.println(
                    """
                    Select option:
                    1. Create Flight
                    2. Create Airplane
                    3. Create Scheduled Flight
                    4. Create Booking
                    """);
            final var option = scanner.nextLine();
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
                default:
            }
        }
    }

    /**
     * The entry point of the application It creates an instance of the {@code App} class and starts
     * the execution of the system.
     *
     * @param args command line arguments(not used)
     */
    public static void main(String[] args) {
        new App().execute();
    }
}
