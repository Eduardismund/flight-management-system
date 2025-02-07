package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to retrieve a list of airplanes.
 */
public class GetAirplanesCommandHandler implements CommandHandler<GetAirplanesCommand, GetAirplanesResponse> {

    /**
     * Handles the GetAirplanesCommand to fetch airplanes and return a response.
     *
     * @param request the GetAirplanesCommand to handle
     * @param service the FlightManagementService used to get the airplanes
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the GetAirplanesResponse containing the list of airplanes and success status
     */
    @Override
    public GetAirplanesResponse handleCommand(
            GetAirplanesCommand request, FlightManagementService service, DomainMapper domainMapper) {

        final var airplanes = service.getAirplanes().stream()
                .map(domainMapper::mapToAirplaneItem)
                .toList();

        final var response = new GetAirplanesResponse();
        response.setAirplanes(airplanes);
        response.setSuccess(true);

        return response;
    }
}
