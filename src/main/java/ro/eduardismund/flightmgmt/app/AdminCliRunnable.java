package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.cli.AdminUi;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

import java.util.Scanner;

@RequiredArgsConstructor
public class AdminCliRunnable implements ApplicationRunnable{
    private final FlightManagementRepository repo;
    private final Scanner scanner;
    private final AdminUi adminUi;

    @Override
    public void run(String[] args) {
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
}
