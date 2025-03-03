package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.dtos.AirplaneItem;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.FindAirplaneCommand;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class FindAirplaneCommandHandlerTest {
    private FindAirplaneCommandHandler handler;
    private FindAirplaneCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new FindAirplaneCommandHandler();
        command = mock(FindAirplaneCommand.class);
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand_isSuccessful() {
        final var airplane = new Airplane("A123");
        final var airplaneItem = new AirplaneItem();
        airplaneItem.setIdNumber("A123");

        doReturn("A123").when(command).getNumber();
        doReturn(Optional.of(airplane)).when(service).findAirplane(anyString());
        doReturn(airplaneItem).when(domainMapper).mapToAirplaneItem(any(Airplane.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isFound());
        assertEquals(airplaneItem, response.getAirplaneItem());

        verify(domainMapper).mapToAirplaneItem(any(Airplane.class));
        verify(service).findAirplane(anyString());
    }

    @Test
    void handleCommand_isNotSuccessful() {
        doReturn(Optional.empty()).when(service).findAirplane(null);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertFalse(response.isFound());
        assertNull(response.getAirplaneItem());

        verify(service).findAirplane(null);
    }
}
