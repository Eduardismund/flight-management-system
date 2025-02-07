package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

public class GetFlightsCommandHandler implements CommandHandler<GetFlightsCommand, GetFlightsResponse> {
    @Override
    public GetFlightsResponse handleCommand(
            GetFlightsCommand request, FlightManagementService service, DomainMapper domainMapper) {
        final var flights =
                service.getFlights().stream().map(domainMapper::mapFlightItem).toList();

        final var response = new GetFlightsResponse();
        response.setFlights(flights);
        response.setSuccess(true);
        return response;
    }
}
