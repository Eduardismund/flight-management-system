package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.FindAirplaneCommand;
import ro.eduardismund.flightmgmt.dtos.FindAirplaneCommandResponse;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to find an airplane.
 */
public class FindAirplaneCommandHandler implements CommandHandler<FindAirplaneCommand, FindAirplaneCommandResponse> {
    /**
     * Handles the FindFlightCommand to fetch an airplane and return a response.
     *
     * @param request the FindAirplaneCommand to handle
     * @param service the FlightManagementService used to get the airplane
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the FindAirplaneCommandResponse containing the airplane and found status
     */
    @Override
    public FindAirplaneCommandResponse handleCommand(
            FindAirplaneCommand request, FlightManagementService service, DomainMapper domainMapper) {
        final var airplaneItem = service.findAirplane(request.getNumber()).map(domainMapper::mapToAirplaneItem);
        final var response = new FindAirplaneCommandResponse();

        if (airplaneItem.isPresent()) {
            response.setAirplaneItem(airplaneItem.get());
            response.setFound(true);
        }

        return response;
    }
}
