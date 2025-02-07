package ro.eduardismund.flightmgmt.service;

import java.util.stream.Collectors;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;
import ro.eduardismund.flightmgmt.server.*;

public class DomainMapper {
    public CreateFlightCommand mapToCreateFlightCommand(Flight flight) {
        final var command = new CreateFlightCommand();
        command.setNumber(flight.getNumber());
        command.setCompany(flight.getCompany());

        return command;
    }

    public CreateAirplaneCommand mapToCreateAirplaneCommand(Airplane airplane) {
        final var command = new CreateAirplaneCommand();
        command.setIdNumber(airplane.getIdNumber());
        command.setModel(airplane.getModel());
        command.setSeatingChart(mapToSeatingChartDto(airplane.getSeatingChart()));
        return command;
    }

    private SeatingChartDto mapToSeatingChartDto(SeatingChart seatingChart) {
        final var seatingChartDto = new SeatingChartDto();
        seatingChart.getSeats().stream().map(this::mapToSeatItem).forEach(seatingChartDto.getSeats()::add);
        return seatingChartDto;
    }

    private SeatItem mapToSeatItem(Seat seat) {
        return new SeatItem(seat.getRow(), seat.getSeatName(), seat.isBusinessClass());
    }

    public Flight mapFromCreateFlightCommand(CreateFlightCommand command) {
        var flight = new Flight(command.getNumber());
        flight.setCompany(command.getCompany());
        return flight;
    }

    public Airplane mapFromCreateAirplaneCommand(CreateAirplaneCommand command) {
        var airplane = new Airplane(command.getIdNumber());
        airplane.setModel(command.getModel());
        airplane.setSeatingChart(mapFromSeatingChartDto(command.getSeatingChart()));
        return airplane;
    }

    public FlightItem mapFlightItem(Flight flight) {
        final var flightItem = new FlightItem();
        flightItem.setCompany(flight.getCompany());
        flightItem.setNumber(flight.getNumber());
        return flightItem;
    }

    public AirplaneItem mapToAirplaneItem(Airplane airplane) {
        final var airplaneItem = new AirplaneItem();
        airplaneItem.setModel(airplane.getModel());
        airplaneItem.setIdNumber(airplane.getIdNumber());
        airplaneItem.setSeatingChart(mapFromSeatingChart(airplane.getSeatingChart()));
        return airplaneItem;
    }

    private SeatingChartDto mapFromSeatingChart(SeatingChart seatingChart) {
        return new SeatingChartDto(
                seatingChart.getSeats().stream().map(this::mapFromSeat).collect(Collectors.toSet()));
    }

    private SeatItem mapFromSeat(Seat seat) {
        return new SeatItem(seat.getRow(), seat.getSeatName(), seat.isBusinessClass());
    }

    private SeatingChart mapFromSeatingChartDto(SeatingChartDto seatingChartDto) {
        return new SeatingChart(
                seatingChartDto.getSeats().size(),
                seatingChartDto.getSeats().stream().map(this::mapFromSeatItem).collect(Collectors.toSet()));
    }

    private Seat mapFromSeatItem(SeatItem seatItem) {
        return new Seat(seatItem.getRow(), seatItem.getSeatName(), seatItem.isBusinessClass());
    }

    public Airplane mapFromAirplaneItem(AirplaneItem airplaneItem) {
        final var airplane = new Airplane(airplaneItem.getIdNumber());
        airplane.setModel(airplaneItem.getModel());
        airplane.setSeatingChart(mapFromSeatingChartDto(airplaneItem.getSeatingChart()));
        return airplane;
    }
}
