package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Interface for handling commands.
 *
 * @param <I> Input type for the command
 * @param <O> Output type after handling the command
 */
public interface CommandHandler<I, O> {

    /**
     * Handles the command with the provided input, service, and domain mapper.
     *
     * @param request        the input request for the command
     * @param service        the flight management service
     * @param domainMapper   the domain mapper
     * @return the output after handling the command
     */
    O handleCommand(I request, FlightManagementService service, DomainMapper domainMapper);
}
