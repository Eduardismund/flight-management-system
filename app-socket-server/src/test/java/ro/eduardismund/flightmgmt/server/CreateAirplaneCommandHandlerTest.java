package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.dtos.CreateAirplaneCommand;
import ro.eduardismund.flightmgmt.dtos.CreateAirplaneResponse;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class CreateAirplaneCommandHandlerTest {
    public static final String ID_NUMBER = "A123";
    private CreateAirplaneCommandHandler handler;
    private CreateAirplaneCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new CreateAirplaneCommandHandler();
        command = new CreateAirplaneCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @SneakyThrows
    @Test
    void handleCommand_isSuccessful() {

        final var a123 = new Airplane(ID_NUMBER);
        doReturn(a123).when(domainMapper).mapFromCreateAirplaneCommand(command);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(a123.getIdNumber(), response.getAirplaneId());
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertInstanceOf(CreateAirplaneResponse.class, response);

        verify(domainMapper).mapFromCreateAirplaneCommand(command);
        verify(service).createAirplane(a123);
    }

    @SneakyThrows
    @Test
    void handleCommand_airplaneAlreadyExists() {

        final var a123 = new Airplane(ID_NUMBER);
        doReturn(a123).when(domainMapper).mapFromCreateAirplaneCommand(command);
        doThrow(new AirplaneAlreadyExistsException(ID_NUMBER)).when(service).createAirplane(a123);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(a123.getIdNumber(), response.getAirplaneId());
        assertFalse(response.isSuccess());
        assertEquals(response.getError(), CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists);
        assertInstanceOf(CreateAirplaneResponse.class, response);

        verify(domainMapper).mapFromCreateAirplaneCommand(command);
        verify(service).createAirplane(a123);
    }

    @SneakyThrows
    @Test
    void handleCommand_internalError() {

        final var a123 = new Airplane(ID_NUMBER);
        doReturn(a123).when(domainMapper).mapFromCreateAirplaneCommand(command);
        doThrow(new RuntimeException()).when(service).createAirplane(a123);
        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertEquals(a123.getIdNumber(), response.getAirplaneId());
        assertFalse(response.isSuccess());
        assertEquals(response.getError(), CreateAirplaneResponse.CarErrorType.InternalError);
        assertInstanceOf(CreateAirplaneResponse.class, response);

        verify(domainMapper).mapFromCreateAirplaneCommand(command);
        verify(service).createAirplane(a123);
    }
}
