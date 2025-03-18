package ro.eduardismund.flightmgmt.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Servlet handling requests regarding Bookings.
 */
@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class BookingServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    final transient XmlManager xmlManager;
    final transient FlightManagementService service;
    final transient DomainMapper domainMapper;

    /**
     * Handle POST requests i.e. booking creation.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException  when an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        final var command = (CreateBookingCommand) xmlManager.unmarshal(req.getReader());
        final var createResponse = new CreateBookingResponse();
        final var scheduledFlight = service.findScheduledFlight(
                        command.getFlightId(), LocalDate.parse(command.getDepartureDate()))
                .get();
        final var scheduledSeat = findScheduledSeat(scheduledFlight, command);
        final var booking = domainMapper.mapFromCreateBookingCommand(command, scheduledFlight, scheduledSeat);
        try {
            service.createBooking(booking);
            createResponse.setSuccess(true);
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) {
            createResponse.setError(CreateBookingResponse.CbrErrorType.InternalError);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        xmlManager.marshal(createResponse, resp.getWriter());
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
