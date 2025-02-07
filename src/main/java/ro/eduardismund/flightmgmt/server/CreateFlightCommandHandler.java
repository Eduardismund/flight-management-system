package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

public class CreateFlightCommandHandler implements CommandHandler<CreateFlightCommand, CreateFlightResponse> {

    @Override
    public CreateFlightResponse handleCommand(
            CreateFlightCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var flight = domainMapper.mapFromCreateFlightCommand(command);
        final var response = new CreateFlightResponse();
        response.setNumber(command.getNumber());
        try {
            service.createFlight(flight);
            response.setSuccess(true);
            response.setError(null);
        } catch (FlightAlreadyExistsException e) {
            response.setSuccess(false);
            response.setError(CreateFlightResponse.CfrErrorType.FlightAlreadyExists);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError(CreateFlightResponse.CfrErrorType.InternalError);
        }
        return response;
    }
}
