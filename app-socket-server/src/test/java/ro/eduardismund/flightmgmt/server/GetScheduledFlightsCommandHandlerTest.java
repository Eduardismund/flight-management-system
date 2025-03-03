package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.GetScheduledFlightsCommand;
import ro.eduardismund.flightmgmt.dtos.ScheduledFlightItem;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class GetScheduledFlightsCommandHandlerTest {
    private GetScheduledFlightsCommandHandler handler;
    private GetScheduledFlightsCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new GetScheduledFlightsCommandHandler();
        command = new GetScheduledFlightsCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand() {
        final var scheduledFlight = List.of(new ScheduledFlight());
        final var scheduledFlightItem = new ScheduledFlightItem();

        doReturn(scheduledFlight).when(service).getScheduledFlights();
        doReturn(scheduledFlightItem).when(domainMapper).mapToScheduledFlightItem(any(ScheduledFlight.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(List.of(scheduledFlightItem), response.getScheduledFlights());

        verify(domainMapper).mapToScheduledFlightItem(any(ScheduledFlight.class));
        verify(service).getScheduledFlights();
    }
}
