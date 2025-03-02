package ro.eduardismund.flightmgmt.server;

import static ro.eduardismund.flightmgmt.dtos.CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists;
import static ro.eduardismund.flightmgmt.dtos.CreateAirplaneResponse.CarErrorType.InternalError;

import ro.eduardismund.flightmgmt.dtos.CreateAirplaneCommand;
import ro.eduardismund.flightmgmt.dtos.CreateAirplaneResponse;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to create a new airplane.
 */
public class CreateAirplaneCommandHandler implements CommandHandler<CreateAirplaneCommand, CreateAirplaneResponse> {

    /**
     * Handles the creation of a new airplane.
     *
     * @param command The {@link CreateAirplaneCommand} containing airplane details.
     * @param service The {@link FlightManagementService} to create the airplane.
     * @param domainMapper The {@link DomainMapper} to map the command to an airplane.
     * @return A {@link CreateAirplaneResponse} with the result of the operation.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public CreateAirplaneResponse handleCommand(
            CreateAirplaneCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var airplane = domainMapper.mapFromCreateAirplaneCommand(command);
        final var response = new CreateAirplaneResponse();
        response.setAirplaneId(airplane.getIdNumber());

        try {
            service.createAirplane(airplane);
            response.setSuccess(true);
        } catch (AirplaneAlreadyExistsException e) {
            response.setError(AirplaneAlreadyExists);
        } catch (Exception e) {
            response.setError(InternalError);
        }
        return response;
    }
}
