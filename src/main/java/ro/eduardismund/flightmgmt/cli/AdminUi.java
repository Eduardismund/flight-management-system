package ro.eduardismund.flightmgmt.cli;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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
import ro.eduardismund.flightmgmt.service.FlightManagementService;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;

/**
 * Administration Command Line Interface.
 */
@RequiredArgsConstructor
public class AdminUi {

    /**
     * Services called by the Admin UI.
     */
    private final FlightManagementService service;

    private final CliManager cliManager;

    /**
     * Create a booking using the user's input.
     */
    public void createBooking() {

        final String flightNumber = cliManager.println("Enter flight number: ").readLine();

        final LocalDate date =
                LocalDate.from(cliManager.readDate("Insert the date in which you want to book your flight: "));

        final Optional<ScheduledFlight> scheduledFlightOpt = service.findScheduledFlight(flightNumber, date);
        if (scheduledFlightOpt.isEmpty()) {
            cliManager.println("Flight " + flightNumber + " not found on " + date);
            return;
        }

        final ScheduledFlight scheduledFlight = scheduledFlightOpt.get();
        cliManager.println("Flight "
                + flightNumber
                + " is departing at: "
                + scheduledFlight.getDepartureTime()
                + " and arriving at: "
                + scheduledFlight.getArrivalTime());

        final String firstName =
                cliManager.println("Enter the first name of the passenger: ").readLine();

        final String lastName =
                cliManager.println("Enter the last name of the passenger: ").readLine();

        final String idDocument =
                cliManager.println("Enter the ID number of the passenger: ").readLine();

        final Passenger passenger = new Passenger(firstName, lastName, idDocument);

        final int row = cliManager.println("Enter the row of the seat: ").readInt();

        final String name = cliManager.println("Enter the name of the seat: ").readLine();

        final Seat seat = new Seat(row, name, true);

        final Booking booking = new Booking();
        booking.setScheduledFlight(scheduledFlight);
        booking.setPassenger(passenger);
        booking.setAssignedSeat(seat);

        service.createBooking(booking);

        cliManager.println("Booking created!");
    }

    /**
     * Inputs the data for a new flight from the keyboard and tries to create a flight as long as it
     * doesn't already exist.
     */
    public void createFlight() {
        cliManager.printAll(service.getFlights(), "The list of Flights: ", "No Flights yet! Ooupsie!");

        final String num = cliManager.println("Insert a flight number: ").readLine();

        final String company = cliManager.println("Insert flight company: ").readLine();

        final Flight flight = new Flight(num);

        flight.setCompany(company);
        try {
            service.createFlight(flight);
            cliManager.println("Flight created");

        } catch (FlightAlreadyExistsException e) {
            cliManager.println("Flight already exists!");
        }
    }

    /**
     * Inputs the data for a new airplane from the keyboard and tries to create an airplane as long as
     * it doesn't already exist.
     */
    public void createAirplane() {
        cliManager.printAll(service.getAirplanes(), "The list of Airplanes: ", "No Airplanes yet!");

        final String idNumber = cliManager.println("Insert ID number: ").readLine();

        final String model = cliManager.println("Insert model: ").readLine();

        final var rows = cliManager.println("Number of rows: ").readInt();

        final var seatsPerRow = cliManager.println("Number of seats per row: ").readInt();

        final var airplane = new Airplane(idNumber);
        airplane.setModel(model);
        airplane.setSeatingChart(new SeatingChart(rows, seatsPerRow));

        try {
            service.createAirplane(airplane);
            cliManager.println("Airplane created");

        } catch (AirplaneAlreadyExistsException e) {
            cliManager.println("Airplane already exists!");
        }
    }

    /**
     * Inputs the data for a new scheduled flight from the keyboard and tries to create a scheduled
     * flight as long as it doesn't already exist.
     */
    public void createScheduledFlight() {

        cliManager.printAll(
                service.getScheduledFlights(), "The list of Scheduled Flights: ", "No Scheduled Flights yet!");
        cliManager.printAll(service.getFlights(), "The list of Flights: ", "No Flights yet! Ooupsie!");
        cliManager.printAll(service.getAirplanes(), "The list of Airplanes: ", "No Airplanes yet!");

        final Flight flight = cliManager.readById("flight", service::findFlight);

        final Airplane airplane = cliManager.readById("airplane", service::findAirplane);

        final LocalDateTime departure = cliManager.readDateTime("departure");

        final LocalDateTime arrival = cliManager.readDateTime("arrival");

        final ScheduledFlight scheduledFlight = new ScheduledFlight();

        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setArrivalTime(arrival);
        scheduledFlight.setDepartureTime(departure);

        try {
            service.createScheduledFlight(scheduledFlight);
            cliManager.println("Scheduled Flight created!");
        } catch (ScheduledFlightAlreadyExistsException
                | AirplaneAlreadyScheduledException
                | ArrivalBeforeDepartureException e) {
            cliManager.println(e.getMessage());
        }
    }
}
