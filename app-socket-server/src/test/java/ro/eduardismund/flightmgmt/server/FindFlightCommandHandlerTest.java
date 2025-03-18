package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.FindFlightCommand;
import ro.eduardismund.flightmgmt.dtos.FlightItem;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class FindFlightCommandHandlerTest {
    private FindFlightCommandHandler handler;
    private FindFlightCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new FindFlightCommandHandler();
        command = mock(FindFlightCommand.class);
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand_isSuccessful() {
        final var flight = new Flight("F123");
        final var flightItem = new FlightItem();
        flightItem.setNumber("F123");

        doReturn("F123").when(command).getNumber();
        doReturn(Optional.of(flight)).when(service).findFlight(anyString());
        doReturn(flightItem).when(domainMapper).mapToFlightItem(any(Flight.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isFound());
        assertEquals(flightItem, response.getFlight());

        verify(domainMapper).mapToFlightItem(any(Flight.class));
        verify(service).findFlight(anyString());
    }

    @Test
    void handleCommand_isNotSuccessful() {
        doReturn(Optional.empty()).when(service).findFlight(null);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertFalse(response.isFound());
        assertNull(response.getFlight());

        verify(service).findFlight(null);
    }
}
