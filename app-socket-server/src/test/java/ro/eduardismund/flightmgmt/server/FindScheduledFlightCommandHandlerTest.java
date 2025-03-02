package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.FindScheduledFlightCommand;
import ro.eduardismund.flightmgmt.dtos.FlightItem;
import ro.eduardismund.flightmgmt.dtos.ScheduledFlightItem;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class FindScheduledFlightCommandHandlerTest {
    public static final String F_123 = "F123";
    private FindScheduledFlightCommandHandler handler;
    private FindScheduledFlightCommand command;
    private FlightManagementService service;
    private DomainMapper domainMapper;

    @BeforeEach
    void setUp() {
        handler = new FindScheduledFlightCommandHandler();
        command = mock(FindScheduledFlightCommand.class);
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
    }

    @Test
    void handleCommand_isSuccessful() {
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(F_123));
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-12-12T12:00"));
        final var scheduledFlightItem = new ScheduledFlightItem();
        FlightItem flightItem = new FlightItem();
        flightItem.setNumber(F_123);
        scheduledFlightItem.setFlight(flightItem);
        scheduledFlightItem.setDepartureTime("2022-12-12T12:00");

        doReturn(F_123).when(command).getNumber();
        doReturn("2022-12-12").when(command).getDepartureDate();
        doReturn(Optional.of(scheduledFlight)).when(service).findScheduledFlight(anyString(), any());
        doReturn(scheduledFlightItem).when(domainMapper).mapToScheduledFlightItem(any(ScheduledFlight.class));

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isFound());
        assertEquals(scheduledFlightItem, response.getScheduledFlightItem());

        verify(domainMapper).mapToScheduledFlightItem(any(ScheduledFlight.class));
        verify(service).findScheduledFlight(anyString(), any());
    }

    @Test
    void handleCommand_isNotSuccessful() {
        doReturn(F_123).when(command).getNumber();
        doReturn("2022-12-12").when(command).getDepartureDate();

        doReturn(Optional.empty()).when(service).findScheduledFlight(anyString(), any());

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertFalse(response.isFound());
        assertNull(response.getScheduledFlightItem());

        verify(service).findScheduledFlight(any(), any());
    }
}
