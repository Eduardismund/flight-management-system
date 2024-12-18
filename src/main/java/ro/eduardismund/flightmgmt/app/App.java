package ro.eduardismund.flightmgmt.app;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.repo.JdbcFlightManagementRepository;
import ro.eduardismund.flightmgmt.service.Service;

/**
 * The {@code App} class is the main entry point of the Flight Management system It interacts with
 * the repository, service and the ui in order to manage flights.
 */
public class App {

    //    InmemFlightManagementRepository repo = new InmemFlightManagementRepository(
    //            new JavaSerializationFlightManagementPersistenceManager(Path.of("database.dat")));
    private Properties properties = loadConfigFile();

    private static final String URL =
            "jdbc:sqlserver://localhost;databaseName=flight_mgmt;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "flight_mgmt";
    private static final String PASSWORD = "flight_mgmt";
    private DataSource dataSource = createDataSource();
    JdbcFlightManagementRepository repo = new JdbcFlightManagementRepository(dataSource);
    Service service = new Service(repo);
    AdminUi adminUi = new AdminUi(service, new CliManager(System.out, new Scanner(System.in, StandardCharsets.UTF_8)));
    private final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    @SneakyThrows
    private static Properties loadConfigFile() {
        final var properties = new Properties();
        try (final var buffer = Files.newBufferedReader(Path.of("config/application.properties"))) {
            properties.load(buffer);
        }
        return properties;
    }

    private DataSource createDataSource() {
        final var dataSource = new SQLServerDataSource();
        dataSource.setURL(properties.getProperty("datasource.url"));
        dataSource.setUser(properties.getProperty("datasource.username"));
        dataSource.setPassword(properties.getProperty("datasource.password"));
        return dataSource;
    }

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
