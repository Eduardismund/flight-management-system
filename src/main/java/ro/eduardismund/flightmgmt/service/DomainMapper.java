package ro.eduardismund.flightmgmt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;
import ro.eduardismund.flightmgmt.server.CreateAirplaneCommand;
import ro.eduardismund.flightmgmt.server.CreateBookingCommand;
import ro.eduardismund.flightmgmt.server.CreateFlightCommand;
import ro.eduardismund.flightmgmt.server.CreateScheduledFlightCommand;
import ro.eduardismund.flightmgmt.server.FindAirplaneCommand;
import ro.eduardismund.flightmgmt.server.FindFlightCommand;
import ro.eduardismund.flightmgmt.server.FindScheduledFlightCommand;
import ro.eduardismund.flightmgmt.server.dtos.AirplaneItem;
import ro.eduardismund.flightmgmt.server.dtos.FlightItem;
import ro.eduardismund.flightmgmt.server.dtos.PassengerItem;
import ro.eduardismund.flightmgmt.server.dtos.ScheduledFlightItem;
import ro.eduardismund.flightmgmt.server.dtos.SeatItem;
import ro.eduardismund.flightmgmt.server.dtos.SeatingChartDto;

/**
 * This class provides mapping methods to convert between domain objects and the corresponding command or DTO objects.
 * It helps in transforming data between the internal model and external communication formats.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods"})
public class DomainMapper {

    /**
     * Maps a {@link CreateBookingCommand} to a {@link Booking}.
     *
     * @param command The {@link CreateBookingCommand} to be mapped.
     * @return A {@link Booking} object.
     */
    public Booking mapFromCreateBookingCommand(CreateBookingCommand command) {
        final var booking = new Booking();
        booking.setPassenger(mapToPassenger(command.getPassenger()));
        booking.setAssignedSeat(mapToSeat(command.getAssignedSeat()));
        booking.setScheduledFlight(mapToScheduledFlight(command.getScheduledFlight()));
        return booking;
    }

    /**
     * Maps a {@link CreateFlightCommand} to a {@link Flight}.
     *
     * @param command The {@link CreateFlightCommand} to be mapped.
     * @return A {@link Flight} object.
     */
    public Flight mapFromCreateFlightCommand(CreateFlightCommand command) {
        final var flight = new Flight(command.getNumber());
        flight.setCompany(command.getCompany());
        return flight;
    }

    /**
     * Maps a {@link CreateScheduledFlightCommand} to a {@link ScheduledFlight}.
     *
     * @param command The {@link CreateScheduledFlightCommand} to be mapped.
     * @return A {@link ScheduledFlight} object.
     */
    public ScheduledFlight mapFromCreateScheduledFlightCommand(
            CreateScheduledFlightCommand command, Flight flight, Airplane airplane) {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);
        scheduledFlight.setArrivalTime(LocalDateTime.parse(command.getArrival()));
        scheduledFlight.setDepartureTime(LocalDateTime.parse(command.getDeparture()));
        return scheduledFlight;
    }

    /**
     * Maps a {@link CreateAirplaneCommand} to an {@link Airplane}.
     *
     * @param command The {@link CreateAirplaneCommand} to be mapped.
     * @return An {@link Airplane} object.
     */
    public Airplane mapFromCreateAirplaneCommand(CreateAirplaneCommand command) {
        final var airplane = new Airplane(command.getIdNumber());
        airplane.setModel(command.getModel());
        airplane.setSeatingChart(mapToSeatingChart(command.getSeatingChart()));
        return airplane;
    }

    /**
     * Converts a flight number into a {@link FindFlightCommand}.
     *
     * @param number The flight number to be converted.
     * @return A {@link FindFlightCommand} object.
     */
    public FindFlightCommand mapToFindFlightCommand(String number) {
        final var command = new FindFlightCommand();
        command.setNumber(number);
        return command;
    }

    /**
     * Converts a flight number and date into a {@link FindScheduledFlightCommand}.
     *
     * @param flightNumber The flight number to search for.
     * @param localDate The date of the flight.
     * @return A {@link FindScheduledFlightCommand} object.
     */
    public FindScheduledFlightCommand mapToFindScheduledFlightCommand(String flightNumber, LocalDate localDate) {
        final FindScheduledFlightCommand command = new FindScheduledFlightCommand();
        command.setDepartureDate(localDate.toString());
        command.setNumber(flightNumber);
        return command;
    }

    /**
     * Converts an airplane ID into a {@link FindAirplaneCommand}.
     *
     * @param idNumber The ID of the airplane to be searched.
     * @return A {@link FindAirplaneCommand} object.
     */
    public FindAirplaneCommand mapToFindAirplaneCommand(String idNumber) {
        final var command = new FindAirplaneCommand();
        command.setNumber(idNumber);
        return command;
    }

    /**
     * Converts a {@link Flight} object into a {@link CreateFlightCommand}.
     *
     * @param flight The {@link Flight} object to be converted.
     * @return A {@link CreateFlightCommand} object.
     */
    public CreateFlightCommand mapToCreateFlightCommand(Flight flight) {
        final var command = new CreateFlightCommand();
        command.setNumber(flight.getNumber());
        command.setCompany(flight.getCompany());
        return command;
    }

    /**
     * Converts a {@link Booking} object into a {@link CreateBookingCommand}.
     *
     * @param booking The {@link Booking} object to be converted.
     * @return A {@link CreateBookingCommand} object.
     */
    public CreateBookingCommand mapToCreateBookingCommand(Booking booking) {
        final var response = new CreateBookingCommand();
        response.setScheduledFlight(mapToScheduledFlightItem(booking.getScheduledFlight()));
        response.setPassenger(mapToPassengerDto(booking.getPassenger()));
        response.setAssignedSeat(mapToSeatItem(booking.getAssignedSeat()));
        return response;
    }

    /**
     * Converts a {@link ScheduledFlight} object into a {@link CreateScheduledFlightCommand}.
     *
     * @param scheduledFlightItem The {@link ScheduledFlight} object to be converted.
     * @return A {@link CreateScheduledFlightCommand} object.
     */
    public CreateScheduledFlightCommand mapToCreateScheduledFlightCommand(ScheduledFlight scheduledFlightItem) {
        final var command = new CreateScheduledFlightCommand();
        command.setAirplane(scheduledFlightItem.getAirplane().getIdNumber());
        command.setFlightId(scheduledFlightItem.getFlight().getNumber());
        command.setDeparture(scheduledFlightItem.getDepartureTime().toString());
        command.setArrival(scheduledFlightItem.getArrivalTime().toString());
        return command;
    }

    /**
     * Converts an {@link Airplane} object into a {@link CreateAirplaneCommand}.
     *
     * @param airplane The {@link Airplane} object to be converted.
     * @return A {@link CreateAirplaneCommand} object.
     */
    public CreateAirplaneCommand mapToCreateAirplaneCommand(Airplane airplane) {
        final var command = new CreateAirplaneCommand();
        command.setIdNumber(airplane.getIdNumber());
        command.setModel(airplane.getModel());
        command.setSeatingChart(mapToSeatingChartDto(airplane.getSeatingChart()));
        return command;
    }

    /**
     * Converts a {@link SeatingChart} object into a {@link SeatingChartDto}.
     *
     * @param seatingChart The {@link SeatingChart} object to be converted.
     * @return A {@link SeatingChartDto} object.
     */
    public SeatingChartDto mapToSeatingChartDto(SeatingChart seatingChart) {
        final var seatingChartDto = new SeatingChartDto();
        seatingChart.getSeats().stream().map(this::mapToSeatItem).forEach(seatingChartDto.getSeats()::add);
        return seatingChartDto;
    }

    /**
     * Converts a {@link Flight} object into a {@link FlightItem}.
     *
     * @param flight The {@link Flight} object to be converted.
     * @return A {@link FlightItem} object.
     */
    public FlightItem mapToFlightItem(Flight flight) {
        final var flightItem = new FlightItem();
        flightItem.setCompany(flight.getCompany());
        flightItem.setNumber(flight.getNumber());
        return flightItem;
    }

    /**
     * Converts a {@link ScheduledFlight} object into a {@link ScheduledFlightItem}.
     *
     * @param flight The {@link ScheduledFlight} object to be converted.
     * @return A {@link ScheduledFlightItem} object.
     */
    public ScheduledFlightItem mapToScheduledFlightItem(ScheduledFlight flight) {
        final var flightItem = new ScheduledFlightItem();
        flightItem.setFlight(mapToFlightItem(flight.getFlight()));
        flightItem.setAirplane(mapToAirplaneItem(flight.getAirplane()));
        flightItem.setDepartureTime(flight.getDepartureTime().toString());
        flightItem.setArrivalTime(flight.getArrivalTime().toString());
        return flightItem;
    }

    /**
     * Converts a {@link Passenger} object into a {@link PassengerItem}.
     *
     * @param passenger The {@link Passenger} object to be converted.
     * @return A {@link PassengerItem} object.
     */
    PassengerItem mapToPassengerDto(Passenger passenger) {
        final var passengerItem = new PassengerItem();
        passengerItem.setFirstName(passenger.getFirstName());
        passengerItem.setLastName(passenger.getLastName());
        passengerItem.setIdDocument(passenger.getIdDocument());
        return passengerItem;
    }

    /**
     * Converts a {@link Seat} object into a {@link SeatItem}.
     *
     * @param seat The {@link Seat} object to be converted.
     * @return A {@link SeatItem} object.
     */
    SeatItem mapToSeatItem(Seat seat) {
        return new SeatItem(seat.getRow(), seat.getSeatName(), seat.isBusinessClass());
    }

    /**
     * Converts an {@link Airplane} object into an {@link AirplaneItem}.
     *
     * @param airplane The {@link Airplane} object to be converted.
     * @return An {@link AirplaneItem} object.
     */
    public AirplaneItem mapToAirplaneItem(Airplane airplane) {
        final var airplaneItem = new AirplaneItem();
        airplaneItem.setModel(airplane.getModel());
        airplaneItem.setIdNumber(airplane.getIdNumber());
        airplaneItem.setSeatingChart(mapToSeatingChartDto(airplane.getSeatingChart()));
        return airplaneItem;
    }

    /**
     * Converts a {@link SeatingChartDto} object into a {@link SeatingChart}.
     *
     * @param seatingChartDto The {@link SeatingChartDto} object to be converted.
     * @return A {@link SeatingChart} object.
     */
    SeatingChart mapToSeatingChart(SeatingChartDto seatingChartDto) {
        return new SeatingChart(
                seatingChartDto.getSeats().size(),
                seatingChartDto.getSeats().stream().map(this::mapToSeat).collect(Collectors.toSet()));
    }

    /**
     * Converts a {@link SeatItem} object into a {@link Seat}.
     *
     * @param seatItem The {@link SeatItem} object to be converted.
     * @return A {@link Seat} object.
     */
    Seat mapToSeat(SeatItem seatItem) {
        return new Seat(seatItem.getRow(), seatItem.getSeatName(), seatItem.isBusinessClass());
    }

    /**
     * Converts an {@link AirplaneItem} object into an {@link Airplane}.
     *
     * @param airplaneItem The {@link AirplaneItem} object to be converted.
     * @return An {@link Airplane} object.
     */
    public Airplane mapToAirplane(AirplaneItem airplaneItem) {
        final var airplane = new Airplane(airplaneItem.getIdNumber());
        airplane.setModel(airplaneItem.getModel());
        airplane.setSeatingChart(mapToSeatingChart(airplaneItem.getSeatingChart()));
        return airplane;
    }

    /**
     * Converts a {@link FlightItem} object into a {@link Flight}.
     *
     * @param flightItem The {@link FlightItem} object to be converted.
     * @return A {@link Flight} object.
     */
    Flight mapToFlight(FlightItem flightItem) {
        final var flight = new Flight(flightItem.getNumber());
        flight.setCompany(flightItem.getCompany());
        return flight;
    }

    /**
     * Converts a {@link ScheduledFlightItem} object into a {@link ScheduledFlight}.
     *
     * @param scheduledFlight The {@link ScheduledFlightItem} object to be converted.
     * @return A {@link ScheduledFlight} object.
     */
    public ScheduledFlight mapToScheduledFlight(ScheduledFlightItem scheduledFlight) {
        final var scheduledFlight1 = new ScheduledFlight();
        scheduledFlight1.setFlight(mapToFlight(scheduledFlight.getFlight()));
        scheduledFlight1.setArrivalTime(LocalDateTime.parse(scheduledFlight.getArrivalTime()));
        scheduledFlight1.setDepartureTime(LocalDateTime.parse(scheduledFlight.getDepartureTime()));
        scheduledFlight1.setAirplane(mapToAirplane(scheduledFlight.getAirplane()));
        return scheduledFlight1;
    }

    /**
     * Converts a {@link PassengerItem} object into a {@link Passenger}.
     *
     * @param passenger The {@link PassengerItem} object to be converted.
     * @return A {@link Passenger} object.
     */
    Passenger mapToPassenger(PassengerItem passenger) {
        return new Passenger(passenger.getFirstName(), passenger.getLastName(), passenger.getIdDocument());
    }
}
