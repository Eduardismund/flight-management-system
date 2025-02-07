package ro.eduardismund.flightmgmt.server;

import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

public interface CommandHandler<Rq, Rs> {
    Rs handleCommand(Rq request, FlightManagementService service, DomainMapper domainMapper);
}
