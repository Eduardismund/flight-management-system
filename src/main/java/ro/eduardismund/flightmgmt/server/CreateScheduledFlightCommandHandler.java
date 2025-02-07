package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.AirplaneAlreadyScheduledException;
import ro.eduardismund.flightmgmt.service.ArrivalBeforeDepartureException;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;

/**
 * Handles the command to create a scheduled flight.
 */
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class CreateScheduledFlightCommandHandler
        implements CommandHandler<CreateScheduledFlightCommand, CreateScheduledFlightResponse> {
    /**
     * Handles the CreateScheduledFlightCommand to fetch an airplane and return a response.
     *
     * @param command      the CreateScheduledFlightCommand to handle
     * @param service      the FlightManagementService used to get the scheduled flight
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the CreateScheduledFlightResponse containing the success status
     */
    @Override
    public CreateScheduledFlightResponse handleCommand(
            CreateScheduledFlightCommand command, FlightManagementService service, DomainMapper domainMapper) {
        final var response = new CreateScheduledFlightResponse();
        try {
            final var flight = service.findFlight(command.getFlightId()).orElseThrow();
            final var airplane = service.findAirplane(command.getAirplane()).orElseThrow();
            final var scheduledFlight = domainMapper.mapFromCreateScheduledFlightCommand(command, flight, airplane);
            service.createScheduledFlight(scheduledFlight);
            response.setFlightId(scheduledFlight.getFlight().getNumber());
            response.setAirplaneId(scheduledFlight.getAirplane().getIdNumber());
            response.setArrivalTime(scheduledFlight.getArrivalTime().toString());
            response.setDepartureTime(scheduledFlight.getDepartureTime().toString());
            response.setSuccess(true);
            return response;
        } catch (ArrivalBeforeDepartureException e) {
            response.setError(CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException);
        } catch (AirplaneAlreadyScheduledException e) {
            response.setError(CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException);
        } catch (ScheduledFlightAlreadyExistsException e) {
            response.setError(CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException);
        } catch (Exception e) {
            response.setError(CreateScheduledFlightResponse.CsfrErrorType.InternalError);
        }
        return response;
    }
}
