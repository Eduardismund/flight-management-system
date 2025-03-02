package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.GetScheduledFlightsCommand;
import ro.eduardismund.flightmgmt.dtos.GetScheduledFlightsResponse;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to retrieve scheduled flights.
 */
public class GetScheduledFlightsCommandHandler
        implements CommandHandler<GetScheduledFlightsCommand, GetScheduledFlightsResponse> {

    /**
     * Handles the GetScheduledFlightsCommand to fetch scheduled flights and return a response.
     *
     * @param command the GetScheduledFlightsCommand to handle
     * @param service the FlightManagementService used to get scheduled flights
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the GetScheduledFlightsResponse containing the scheduled flights and success status
     */
    @Override
    public GetScheduledFlightsResponse handleCommand(
            GetScheduledFlightsCommand command, FlightManagementService service, DomainMapper domainMapper) {

        final var scheduledFlights = service.getScheduledFlights().stream()
                .map(domainMapper::mapToScheduledFlightItem)
                .toList();

        final var response = new GetScheduledFlightsResponse();
        response.setScheduledFlights(scheduledFlights);
        response.setSuccess(true);

        return response;
    }
}
