package ro.eduardismund.flightmgmt.server;

import static ro.eduardismund.flightmgmt.server.CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists;
import static ro.eduardismund.flightmgmt.server.CreateAirplaneResponse.CarErrorType.InternalError;

import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

public class CreateAirplaneCommandHandler implements CommandHandler<CreateAirplaneCommand, CreateAirplaneResponse> {

    @Override
    public CreateAirplaneResponse handleCommand(
            CreateAirplaneCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var airplane = domainMapper.mapFromCreateAirplaneCommand(command);
        final var response = new CreateAirplaneResponse();
        response.setAirplaneId(airplane.getIdNumber());

        try {
            service.createAirplane(airplane);
            response.setSuccess(true);
            response.setError(null);
        } catch (AirplaneAlreadyExistsException e) {
            response.setSuccess(false);
            response.setError(AirplaneAlreadyExists);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError(InternalError);
        }
        return response;
    }
}
