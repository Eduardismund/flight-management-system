package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class CreateBookingCommandHandlerTest {
    private CreateBookingCommandHandler handler;
    private CreateBookingCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new CreateBookingCommandHandler();
        command = new CreateBookingCommand();
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand() {
        final var booking = new Booking();
        doReturn(booking).when(domainMapper).mapFromCreateBookingCommand(command);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertInstanceOf(CreateBookingResponse.class, response);

        verify(domainMapper).mapFromCreateBookingCommand(command);
        verify(service).createBooking(booking);
    }
}
