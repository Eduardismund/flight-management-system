package ro.eduardismund.flightmgmt.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Servlet handling requests regarding Airplanes.
 */
@RequiredArgsConstructor
public class AirplanesServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    final transient XmlManager xmlManager;
    final transient FlightManagementService service;
    final transient DomainMapper domainMapper;
    static final Pattern URI_WITH_ID = Pattern.compile("^/airplanes/([^/]+)$");

    /**
     * Handle GET request both for airplanes list and airplane by id.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException when an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        final var matcher = URI_WITH_ID.matcher(req.getRequestURI());

        if (matcher.matches()) {
            final var airplaneId = matcher.group(1);
            final var airplane = service.findAirplane(airplaneId);
            final var findResponse = new FindAirplaneCommandResponse();

            if (airplane.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                xmlManager.marshal(findResponse, resp.getWriter());
                return;
            }

            final var airplaneItem = domainMapper.mapToAirplaneItem(airplane.get());
            findResponse.setAirplaneItem(airplaneItem);
            findResponse.setFound(true);
            xmlManager.marshal(findResponse, resp.getWriter());
        } else {
            final var airplanes = service.getAirplanes();
            final var getAirplanesResponse = new GetAirplanesResponse();
            airplanes.forEach(
                    airplane -> getAirplanesResponse.getAirplanes().add(domainMapper.mapToAirplaneItem(airplane)));
            getAirplanesResponse.setSuccess(true);
            xmlManager.marshal(getAirplanesResponse, resp.getWriter());
        }
    }

    /**
     * Handle POST request i.e. airplane creation.
     *
     * @param req an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param resp an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @throws IOException when an I/O error occurs
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/xml");
        final var airplaneItem = (CreateAirplaneCommand) xmlManager.unmarshal(req.getReader());

        final var createResponse = new CreateAirplaneResponse();
        createResponse.setAirplaneId(airplaneItem.getIdNumber());

        final var airplane = domainMapper.mapFromCreateAirplaneCommand(airplaneItem);
        try {
            service.createAirplane(airplane);
            createResponse.setSuccess(true);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (AirplaneAlreadyExistsException e) {
            createResponse.setError(CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            createResponse.setError(CreateAirplaneResponse.CarErrorType.InternalError);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        xmlManager.marshal(createResponse, resp.getWriter());
    }
}
