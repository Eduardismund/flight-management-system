package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ro.eduardismund.flightmgmt.domain.*;
import ro.eduardismund.flightmgmt.dtos.CreateBookingCommand;
import ro.eduardismund.flightmgmt.dtos.CreateBookingResponse;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
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
        final var sfMock = mock(ScheduledFlight.class);
        final var sfMockOptional = Optional.of(sfMock);
        final var seat = mock(Seat.class);
        final var airplane = mock(Airplane.class);
        final var seatingChart = mock(SeatingChart.class);
        command.setSeatName("A");
        command.setDepartureDate("2022-12-12");
        command.setSeatRow(1);
        command.setFlightId("F123");
        doReturn(sfMockOptional).when(service).findScheduledFlight(eq("F123"), eq(LocalDate.parse("2022-12-12")));
        doReturn(airplane).when(sfMock).getAirplane();
        doReturn(seatingChart).when(airplane).getSeatingChart();
        doReturn(Set.of(seat)).when(seatingChart).getSeats();
        doReturn("A").when(seat).getSeatName();
        doReturn(1).when(seat).getRow();

        doReturn(booking).when(domainMapper).mapFromCreateBookingCommand(command, sfMock, seat);

        final var response = handler.handleCommand(command, service, domainMapper);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertInstanceOf(CreateBookingResponse.class, response);

        verify(domainMapper).mapFromCreateBookingCommand(command, sfMock, seat);
        verify(service).createBooking(booking);
    }

    @Test
    void findScheduledSeat_notFound() {
        final var scheduledFlight = new ScheduledFlight();
        final var airplane = new Airplane("A123");
        scheduledFlight.setAirplane(airplane);

        final var seatingChart = new SeatingChart(2, 2);
        airplane.setSeatingChart(seatingChart);

        command = new CreateBookingCommand();
        command.setSeatName("Z");
        command.setSeatRow(100);

        assertThrows(
                NoSuchElementException.class,
                () -> CreateBookingCommandHandler.findScheduledSeat(scheduledFlight, command));
    }

    @ParameterizedTest
    @CsvSource({"1,A,1,A,true", "1,B,1,C,false", "2,A,1,A,false", "1,A,2,B,false"})
    void isSameSeat(int seatRow, String seatName, int cmdRow, String cmdSeatName, boolean expected) {
        final var command = new CreateBookingCommand();
        command.setSeatName(cmdSeatName);
        command.setSeatRow(cmdRow);

        final var seat = new Seat(seatRow, seatName, true);

        assertEquals(expected, CreateBookingCommandHandler.isSameSeat(command, seat));
    }
}
