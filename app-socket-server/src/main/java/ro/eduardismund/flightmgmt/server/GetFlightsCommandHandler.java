package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.GetFlightsCommand;
import ro.eduardismund.flightmgmt.dtos.GetFlightsResponse;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to retrieve a list of flights.
 */
public class GetFlightsCommandHandler implements CommandHandler<GetFlightsCommand, GetFlightsResponse> {

    /**
     * Handles the GetFlightsCommand to fetch flights and return a response.
     *
     * @param request the GetFlightsCommand to handle
     * @param service the FlightManagementService used to get the flights
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the GetFlightsResponse containing the list of flights and success status
     */
    @Override
    public GetFlightsResponse handleCommand(
            GetFlightsCommand request, FlightManagementService service, DomainMapper domainMapper) {

        final var flights =
                service.getFlights().stream().map(domainMapper::mapToFlightItem).toList();

        final var response = new GetFlightsResponse();
        response.setFlights(flights);
        response.setSuccess(true);

        return response;
    }
}
