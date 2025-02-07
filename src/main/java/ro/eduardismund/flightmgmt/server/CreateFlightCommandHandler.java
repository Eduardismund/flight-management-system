package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to find a flight.
 */
public class CreateFlightCommandHandler implements CommandHandler<CreateFlightCommand, CreateFlightResponse> {

    /**
     * Handles the creation of a new flight.
     *
     * @param command The {@link CreateFlightCommand} containing flight details.
     * @param service The {@link FlightManagementService} to create the flight.
     * @param domainMapper The {@link DomainMapper} to map the command to a flight.
     * @return A {@link CreateFlightResponse} with the result of the operation.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @Override
    public CreateFlightResponse handleCommand(
            CreateFlightCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var flight = domainMapper.mapFromCreateFlightCommand(command);
        final var response = new CreateFlightResponse();
        response.setNumber(flight.getNumber());
        try {
            service.createFlight(flight);
            response.setSuccess(true);
        } catch (FlightAlreadyExistsException e) {
            response.setError(CreateFlightResponse.CfrErrorType.FlightAlreadyExists);
        } catch (Exception e) {
            response.setError(CreateFlightResponse.CfrErrorType.InternalError);
        }
        return response;
    }
}
