package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.*;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class BookingServletTest {
    public static final String FLIGHT_NUMBER = "F123";
    public static final String SUCCESS = "success";
    public static final String INTERNAL_ERROR = "InternalError";
    private XmlManager xmlManager;
    private FlightManagementService service;
    private BookingServlet subject;
    private DomainMapper domainMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        xmlManager = mock(XmlManager.class);
        domainMapper = mock(DomainMapper.class);
        service = mock(FlightManagementService.class);

        subject = new BookingServlet(xmlManager, service, domainMapper);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @ParameterizedTest
    @CsvSource({SUCCESS, INTERNAL_ERROR})
    void doPost(String type) throws IOException {
        final var command = new CreateBookingCommand();
        final var captor = ArgumentCaptor.forClass(CreateBookingResponse.class);
        final var reader = mock(BufferedReader.class);
        final var writer = mock(PrintWriter.class);
        doReturn(reader).when(request).getReader();
        doReturn(writer).when(response).getWriter();

        final var booking = new Booking();
        final var sfMock = mock(ScheduledFlight.class);
        final var sfMockOptional = Optional.of(sfMock);
        final var seat = mock(Seat.class);
        final var airplane = mock(Airplane.class);
        final var seatingChart = mock(SeatingChart.class);
        command.setSeatName("A");
        command.setDepartureDate("2022-12-12");
        command.setSeatRow(1);
        command.setFlightId(FLIGHT_NUMBER);
        doReturn(sfMockOptional)
                .when(service)
                .findScheduledFlight(eq(FLIGHT_NUMBER), eq(LocalDate.parse("2022-12-12")));
        doReturn(airplane).when(sfMock).getAirplane();
        doReturn(seatingChart).when(airplane).getSeatingChart();
        doReturn(Set.of(seat)).when(seatingChart).getSeats();
        doReturn("A").when(seat).getSeatName();
        doReturn(1).when(seat).getRow();

        doReturn(command).when(xmlManager).unmarshal(reader);
        doReturn(booking).when(domainMapper).mapFromCreateBookingCommand(command, sfMock, seat);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        if (SUCCESS.equals(type)) {
            doNothing().when(service).createBooking(booking);
        } else if (INTERNAL_ERROR.equals(type)) {
            doThrow(RuntimeException.class).when(service).createBooking(booking);
        }
        subject.doPost(request, response);

        if (SUCCESS.equals(type)) {
            assertTrue(captor.getValue().isSuccess());
            verify(response).setStatus(HttpServletResponse.SC_CREATED);
        } else if (INTERNAL_ERROR.equals(type)) {
            verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            assertEquals(
                    CreateBookingResponse.CbrErrorType.InternalError,
                    captor.getValue().getError());
        }
        verify(response).setContentType("text/xml");
        assertInstanceOf(CreateBookingResponse.class, captor.getValue());
    }

    @Test
    void findScheduledSeat_notFound() {
        final var scheduledFlight = new ScheduledFlight();
        final var airplane = new Airplane("A123");
        scheduledFlight.setAirplane(airplane);

        final var seatingChart = new SeatingChart(2, 2);
        airplane.setSeatingChart(seatingChart);

        final var command = new CreateBookingCommand();
        command.setSeatName("Z");
        command.setSeatRow(100);

        assertThrows(NoSuchElementException.class, () -> BookingServlet.findScheduledSeat(scheduledFlight, command));
    }

    @ParameterizedTest
    @CsvSource({"1,A,1,A,true", "1,B,1,C,false", "2,A,1,A,false", "1,A,2,B,false"})
    void isSameSeat(int seatRow, String seatName, int cmdRow, String cmdSeatName, boolean expected) {
        final var command = new CreateBookingCommand();
        command.setSeatName(cmdSeatName);
        command.setSeatRow(cmdRow);

        final var seat = new Seat(seatRow, seatName, true);

        assertEquals(expected, BookingServlet.isSameSeat(command, seat));
    }
}
