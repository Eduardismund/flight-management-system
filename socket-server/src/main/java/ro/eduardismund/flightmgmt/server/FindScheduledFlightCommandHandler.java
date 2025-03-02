package ro.eduardismund.flightmgmt.server;

import java.time.LocalDate;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Handles the command to find a scheduled flight.
 */
public class FindScheduledFlightCommandHandler
        implements CommandHandler<FindScheduledFlightCommand, FindScheduledFlightResponse> {
    /**
     * Handles the FindScheduledFlightCommand to fetch a scheduledFlight and return a response.
     *
     * @param request the FindScheduledFlightCommand to handle
     * @param service the FlightManagementService used to get the scheduledFlight
     * @param domainMapper the DomainMapper to map domain objects to response items
     * @return the FindScheduledFlightResponse containing the scheduledFlight and found status
     */
    @Override
    public FindScheduledFlightResponse handleCommand(
            FindScheduledFlightCommand request, FlightManagementService service, DomainMapper domainMapper) {
        final var scheduledFlightItem = service.findScheduledFlight(
                        request.getNumber(), LocalDate.parse(request.getDepartureDate()))
                .map(domainMapper::mapToScheduledFlightItem);
        final var response = new FindScheduledFlightResponse();

        if (scheduledFlightItem.isPresent()) {
            response.setScheduledFlightItem(scheduledFlightItem.get());
            response.setFound(true);
        }

        return response;
    }
}
