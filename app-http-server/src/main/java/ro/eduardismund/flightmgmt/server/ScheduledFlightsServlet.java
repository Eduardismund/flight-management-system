package ro.eduardismund.flightmgmt.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyScheduledException;
import ro.eduardismund.flightmgmt.service.ArrivalBeforeDepartureException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;

/**
 * Servlet handling requests regarding scheduled flights.
 */
@RequiredArgsConstructor
public class ScheduledFlightsServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    final transient XmlManager xmlManager;
    final transient FlightManagementService service;
    final transient DomainMapper domainMapper;

    /**
     * Handle GET requests both scheduled flight list and scheduled flight by id and date.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException  when an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        final var flightId = req.getParameter("flight-id");
        final var departureDate = req.getParameter("departure-date");
        if (Stream.<Object>of(flightId, departureDate).noneMatch(Objects::isNull)) {
            final var scheduledFlight = service.findScheduledFlight(flightId, LocalDate.parse(departureDate));
            if (scheduledFlight.isEmpty()) {
                final var findResponse = new FindScheduledFlightResponse();
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                xmlManager.marshal(findResponse, resp.getWriter());
                return;
            }

            final var scheduledFlightItem = domainMapper.mapToScheduledFlightItem(scheduledFlight.get());
            final var findResponse = new FindScheduledFlightResponse();
            findResponse.setScheduledFlightItem(scheduledFlightItem);
            findResponse.setFound(true);
            xmlManager.marshal(findResponse, resp.getWriter());
        } else {
            final var flights = service.getScheduledFlights();
            final var getFlightsResponse = new GetScheduledFlightsResponse();
            flights.forEach(flight ->
                    getFlightsResponse.getScheduledFlights().add(domainMapper.mapToScheduledFlightItem(flight)));
            getFlightsResponse.setSuccess(true);
            xmlManager.marshal(getFlightsResponse, resp.getWriter());
        }
    }

    /**
     * Handle POST requests i.e. scheduled Flight creation.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException  when an I/O error occurs
     */
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("text/xml");
        final var scheduledFlightItem = (CreateScheduledFlightCommand) xmlManager.unmarshal(req.getReader());

        final var createResponse = new CreateScheduledFlightResponse();
        createResponse.setFlightId(scheduledFlightItem.getFlightId());
        createResponse.setAirplaneId(scheduledFlightItem.getAirplane());
        createResponse.setArrivalTime(scheduledFlightItem.getArrival());
        createResponse.setDepartureTime(scheduledFlightItem.getDeparture());

        final var airplane =
                service.findAirplane(scheduledFlightItem.getAirplane()).get();
        final var flight = service.findFlight(scheduledFlightItem.getFlightId()).get();
        final var scheduledFlight =
                domainMapper.mapFromCreateScheduledFlightCommand(scheduledFlightItem, flight, airplane);
        try {
            service.createScheduledFlight(scheduledFlight);
            createResponse.setSuccess(true);
        } catch (ScheduledFlightAlreadyExistsException e) {
            createResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ArrivalBeforeDepartureException e) {
            createResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (AirplaneAlreadyScheduledException e) {
            createResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            createResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.InternalError);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        xmlManager.marshal(createResponse, resp.getWriter());
    }
}
