package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.server.dtos.FlightItem;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class GetFlightsCommandHandlerTest {
    private GetFlightsCommandHandler handler;
    private GetFlightsCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new GetFlightsCommandHandler();
        command = new GetFlightsCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand() {
        final var flights = List.of(new Flight("F123"));
        final var flightItem = new FlightItem();
        flightItem.setNumber("F123");
        doReturn(flights).when(service).getFlights();
        doReturn(flightItem).when(domainMapper).mapToFlightItem(any(Flight.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(List.of(flightItem), response.getFlights());

        verify(domainMapper).mapToFlightItem(any(Flight.class));
        verify(service).getFlights();
    }
}
