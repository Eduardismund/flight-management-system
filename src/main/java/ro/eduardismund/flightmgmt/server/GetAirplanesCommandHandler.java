package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

public class GetAirplanesCommandHandler implements CommandHandler<GetAirplanesCommand, GetAirplanesResponse> {
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
