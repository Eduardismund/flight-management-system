package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class CreateFlightCommandHandlerTest {
    private CreateFlightCommandHandler handler;
    private CreateFlightCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new CreateFlightCommandHandler();
        command = new CreateFlightCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @SneakyThrows
    @Test
    void handleCommand_isSuccessful() {

        final var f123 = new Flight("F123");
        doReturn(f123).when(domainMapper).mapFromCreateFlightCommand(command);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(f123.getNumber(), response.getNumber());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertInstanceOf(CreateFlightResponse.class, response);

        verify(domainMapper).mapFromCreateFlightCommand(command);
        verify(service).createFlight(f123);
    }

    @SneakyThrows
    @Test
    void handleCommand_FlightAlreadyExists() {

        final var f123 = new Flight("F123");
        doReturn(f123).when(domainMapper).mapFromCreateFlightCommand(command);
        doThrow(new FlightAlreadyExistsException("A123")).when(service).createFlight(f123);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(f123.getNumber(), response.getNumber());
        assertFalse(response.isSuccess());
        assertEquals(response.getError(), CreateFlightResponse.CfrErrorType.FlightAlreadyExists);
        assertInstanceOf(CreateFlightResponse.class, response);

        verify(domainMapper).mapFromCreateFlightCommand(command);
        verify(service).createFlight(f123);
    }

    @SneakyThrows
    @Test
    void handleCommand_internalError() {

        final var f123 = new Flight("f123");
        doReturn(f123).when(domainMapper).mapFromCreateFlightCommand(command);
        doThrow(new RuntimeException()).when(service).createFlight(f123);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(f123.getNumber(), response.getNumber());
        assertFalse(response.isSuccess());
        assertEquals(response.getError(), CreateFlightResponse.CfrErrorType.InternalError);
        assertInstanceOf(CreateFlightResponse.class, response);

        verify(domainMapper).mapFromCreateFlightCommand(command);
        verify(service).createFlight(f123);
    }
}
