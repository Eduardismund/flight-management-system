package ro.eduardismund.flightmgmt.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Servlet handling requests regarding Flights.
 */
@RequiredArgsConstructor
public class FlightsServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    final transient XmlManager xmlManager;
    final transient FlightManagementService service;
    final transient DomainMapper domainMapper;
    static final Pattern URI_WITH_ID = Pattern.compile("^/flights/([^/]+)$");

    /**
     * Handle GET requests both for flight lists and flight.
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
        final var matcher = URI_WITH_ID.matcher(req.getRequestURI());

        if (matcher.matches()) {
            final var flightId = matcher.group(1);
            final var flight = service.findFlight(flightId);
            if (flight.isEmpty()) {
                final var findResponse = new FindFlightResponse();
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                xmlManager.marshal(findResponse, resp.getWriter());
                return;
            }

            final var flightItem = domainMapper.mapToFlightItem(flight.get());
            final var findResponse = new FindFlightResponse();
            findResponse.setFlight(flightItem);
            findResponse.setFound(true);
            xmlManager.marshal(findResponse, resp.getWriter());
        } else {
            final var flights = service.getFlights();
            final var getFlightsResponse = new GetFlightsResponse();
            flights.forEach(flight -> getFlightsResponse.getFlights().add(domainMapper.mapToFlightItem(flight)));
            getFlightsResponse.setSuccess(true);
            xmlManager.marshal(getFlightsResponse, resp.getWriter());
        }
    }

    /**
     * Handle POST requests i.e. flight creation.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException  when an I/O error occurs
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        final var flightItem = (CreateFlightCommand) xmlManager.unmarshal(req.getReader());

        final var createResponse = new CreateFlightResponse();
        createResponse.setNumber(flightItem.getNumber());

        final var flight = domainMapper.mapFromCreateFlightCommand(flightItem);
        try {
            service.createFlight(flight);
            createResponse.setSuccess(true);
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (FlightAlreadyExistsException e) {
            createResponse.setError(CreateFlightResponse.CfrErrorType.FlightAlreadyExists);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            createResponse.setError(CreateFlightResponse.CfrErrorType.InternalError);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        xmlManager.marshal(createResponse, resp.getWriter());
    }
}
