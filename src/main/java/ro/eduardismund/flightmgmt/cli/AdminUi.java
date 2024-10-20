package ro.eduardismund.flightmgmt.cli;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyScheduledException;
import ro.eduardismund.flightmgmt.service.ArrivalBeforeDepartureException;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.Service;

/** Administration Command Line Interface. */
@RequiredArgsConstructor
public class AdminUi {

    /** Services called by the Admin UI. */
    private final Service service;

    /** Input scanner. */
    private final Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);

    /**
     * Print a list.
     *
     * @param entities the list to print
     */
    public void printAll(List<?> entities) {
        for (Object entity : entities) {
            System.out.println(entity.toString());
        }
    }

    /** Create a booking using the user's input. */
    public void createBooking() {

        System.out.print("Enter flight number: ");
        String flightNumber = input.nextLine();

        LocalDate date = readDate();

        Optional<ScheduledFlight> scheduledFlightOptional = service.findScheduledFlight(flightNumber, date);
        if (scheduledFlightOptional.isEmpty()) {
            System.out.println("Flight " + flightNumber + " not found on " + date);
            return;
        }

        ScheduledFlight scheduledFlight = scheduledFlightOptional.get();
        System.out.println("Flight "
                + flightNumber
                + " is departing at: "
                + scheduledFlight.getDepartureTime()
                + " and arriving at: "
                + scheduledFlight.getArrivalTime());

        System.out.println("Enter the first name of the passenger: ");
        final String firstName = input.nextLine();

        System.out.println("Enter the last name of the passenger: ");
        String lastName = input.nextLine();

        System.out.println("Enter the ID number of the passenger: ");
        String idDocument = input.nextLine();

        final Passenger passenger = new Passenger(firstName, lastName, idDocument);

        System.out.println("Enter the row of the seat: ");
        int row = Integer.parseInt(input.nextLine());

        System.out.println("Enter the name of the seat: ");
        String name = input.nextLine();

        final Seat seat = new Seat(row, name, true);

        Booking booking = new Booking();
        booking.setScheduledFlight(scheduledFlight);
        booking.setPassenger(passenger);
        booking.setAssignedSeat(seat);

        service.createBooking(booking);
    }

    /**
     * Reads the date of the flight, making sure the format is respected.
     *
     * @return the date of the flight
     */
    private LocalDate readDate() {
        LocalDate date = null;
        do {
            System.out.println("Insert the date of the flight: ");
            try {
                date = LocalDate.parse(input.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Try again.");
            }
        } while (date == null);
        return date;
    }

    /**
     * Inputs the data for a new flight from the keyboard and tries to create a flight as long as it
     * doesn't already exist.
     */
    public void createFlight() {
        if (service.getFlights().stream().count() == 0) {
            System.out.println("No flights yet!");
        } else {
            System.out.print("The list of flights: ");
            printAll(service.getFlights());
        }

        System.out.println("Insert a flight number: ");
        String num = input.nextLine();

        System.out.println("Insert flight company: ");
        String company = input.nextLine();

        Flight flight = new Flight(num);
        flight.setCompany(company);
        try {
            service.createFlight(flight);
            System.out.println("Flight created");

        } catch (FlightAlreadyExistsException e) {
            System.out.println("Flight already exists!");
        }
    }

    /**
     * Inputs the data for a new airplane from the keyboard and tries to create an airplane as long as
     * it doesn't already exist.
     */
    public void createAirplane() {
        if (service.getAirplanes().stream().count() == 0) {
            System.out.println("No airplanes yet!");
        } else {
            System.out.println("The list of airplanes: ");
            printAll(service.getFlights());
        }

        System.out.println("Insert ID number: ");

        System.out.println("Insert model: ");

        System.out.println("Number of rows: ");
        final var rows = input.nextInt();
        input.nextLine();

        System.out.println("Number of seats per row: ");
        final var seatsPerRow = input.nextInt();
        input.nextLine();

        String id = input.nextLine();
        final var airplane = new Airplane(id);

        String model = input.nextLine();
        airplane.setModel(model);
        airplane.setSeatingChart(new SeatingChart(rows, seatsPerRow));

        try {
            service.createAirplane(airplane);
            System.out.println("Airplane created");

        } catch (AirplaneAlreadyExistsException e) {
            System.out.println("Airplane already exists!");
        }
    }

    /**
     * Inputs the data for a new scheduled flight from the keyboard and tries to create a scheduled
     * flight as long as it doesn't already exist.
     */
    public void createScheduledFlight() {

        if (service.getFlights().stream().count() == 0
                || service.getFlights().stream().count() == 0) {
            System.out.println("There are not enough airplanes or flights!");
            return;
        }

        System.out.println("The list of flights: ");
        printAll(service.getFlights());
        System.out.println("The list of airplanes: ");
        printAll(service.getAirplanes());
        System.out.println("The list of scheduled flights: ");
        printAll(service.getScheduledFlights());

        Flight flight = readById("flight", service::findFlight);

        Airplane airplane = readById("airplane", service::findAirplane);

        LocalDateTime departure = readDateTimeInput("departure");

        LocalDateTime arrival = readDateTimeInput("arrival");

        ScheduledFlight scheduledFlight = new ScheduledFlight();

        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setArrivalTime(arrival);
        scheduledFlight.setDepartureTime(departure);

        try {
            service.createScheduledFlight(scheduledFlight);
        } catch (ScheduledFlightAlreadyExistsException
                | AirplaneAlreadyScheduledException
                | ArrivalBeforeDepartureException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * function that uses a template in order to find a flight or an airplane in the repository.
     *
     * @param idName of the required entity
     * @param findMethod of the required entity
     * @param <T> Flight/Airplane
     * @return either a Flight or an Airplane
     */
    private <T> T readById(String idName, Function<String, Optional<T>> findMethod) {
        Optional<T> optional;

        do {
            System.out.println("Insert " + idName + " number: ");
            final String num = input.nextLine();

            optional = findMethod.apply(num);

            if (optional.isEmpty()) {
                System.out.println(idName + " with ID " + num + " not found. Please try again.");
            }

        } while (optional.isEmpty());

        return optional.get();
    }

    /**
     * Reads the data input from the keyboard for the date in the correct format.
     *
     * @param type either arrival or departure
     * @return the date of the departure/arrival
     */
    private LocalDateTime readDateTimeInput(String type) {
        Optional<LocalDateTime> dateTime;
        do {
            System.out.println("Insert" + type + " date: YYYY-MM-DDTHH:mm:ss");
            try {
                dateTime = Optional.of(LocalDateTime.parse(input.nextLine()));
            } catch (DateTimeParseException ignored) {
                dateTime = Optional.empty();
            }
        } while (dateTime.isEmpty());
        return dateTime.get();
    }
}
