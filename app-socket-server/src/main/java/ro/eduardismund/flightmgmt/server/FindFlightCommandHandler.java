package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.FindFlightCommand;
import ro.eduardismund.flightmgmt.dtos.FindFlightResponse;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to find a flight.
 */
public class FindFlightCommandHandler implements CommandHandler<FindFlightCommand, FindFlightResponse> {
    /**
     * Handles the FindFlightCommand to fetch a flight and return a response.
     *
     * @param request the FindFlightCommand to handle
     * @param service the FlightManagementService used to get the flight
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the FindFlightResponse containing the flight and found status
     */
    @Override
    public FindFlightResponse handleCommand(
            FindFlightCommand request, FlightManagementService service, DomainMapper domainMapper) {
        final var flight = service.findFlight(request.getNumber()).map(domainMapper::mapToFlightItem);
        final var response = new FindFlightResponse();

        if (flight.isPresent()) {
            response.setFlight(flight.get());
            response.setSuccess(true);
        }

        return response;
    }
}
