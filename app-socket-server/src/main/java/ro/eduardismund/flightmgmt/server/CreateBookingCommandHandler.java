package ro.eduardismund.flightmgmt.server;

import java.time.LocalDate;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.dtos.CreateBookingCommand;
import ro.eduardismund.flightmgmt.dtos.CreateBookingResponse;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to create a new booking.
 */
public class CreateBookingCommandHandler implements CommandHandler<CreateBookingCommand, CreateBookingResponse> {

    /**
     * Handles the creation of a new booking.
     *
     * @param command The {@link CreateBookingCommand} containing booking details.
     * @param service The {@link FlightManagementService} to create the booking.
     * @param domainMapper The {@link DomainMapper} to map the command to a booking.
     * @return A {@link CreateBookingResponse} with the result of the operation.
     */
    @Override
    public CreateBookingResponse handleCommand(
            CreateBookingCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var scheduledFlight = service.findScheduledFlight(
                        command.getFlightId(), LocalDate.parse(command.getDepartureDate()))
                .get();
        final var scheduledSeat = findScheduledSeat(scheduledFlight, command);
        final var booking = domainMapper.mapFromCreateBookingCommand(command, scheduledFlight, scheduledSeat);
        final var response = new CreateBookingResponse();
        service.createBooking(booking);
        response.setSuccess(true);
        return response;
    }

    static Seat findScheduledSeat(ScheduledFlight scheduledFlight, CreateBookingCommand command) {
        return scheduledFlight.getAirplane().getSeatingChart().getSeats().stream()
                .filter(seat -> isSameSeat(command, seat))
                .findAny()
                .get();
    }

    static boolean isSameSeat(CreateBookingCommand command, Seat seat) {
        return seat.getSeatName().equals(command.getSeatName()) && seat.getRow() == command.getSeatRow();
    }
}
