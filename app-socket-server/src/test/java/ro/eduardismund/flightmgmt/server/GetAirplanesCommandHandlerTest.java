package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.dtos.AirplaneItem;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.GetAirplanesCommand;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class GetAirplanesCommandHandlerTest {
    private GetAirplanesCommandHandler handler;
    private GetAirplanesCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new GetAirplanesCommandHandler();
        command = new GetAirplanesCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand() {
        final var airplanes = List.of(new Airplane("A123"));
        final var airplaneItem = new AirplaneItem();
        airplaneItem.setIdNumber("A123");

        doReturn(airplanes).when(service).getAirplanes();
        doReturn(airplaneItem).when(domainMapper).mapToAirplaneItem(any(Airplane.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(List.of(airplaneItem), response.getAirplanes());

        verify(domainMapper).mapToAirplaneItem(any(Airplane.class));
        verify(service).getAirplanes();
    }
}
