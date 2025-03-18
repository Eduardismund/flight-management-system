package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;

class DomainMapperTest {
    public static final String COMPANY = "Tarom";
    public static final String F_123 = "F123";
    public static final String A_123 = "A123";
    public static final String CARGO = "Cargo";
    public static final String ARRIVAL_TIME = "2025-02-20T12:00:00";
    public static final String DEPARTURE_TIME = "2025-02-20T10:00:00";
    public static final String DEPARTURE_DATE = "2022-12-12";
    public static final String FIRST_NAME = "John";
    public static final String DOE = "Doe";
    public static final String LAST_NAME = DOE;
    public static final String ID_DOCUMENT = "12345";
    public static final String COMPANY1 = "WizzAir";
    DomainMapper subject = new DomainMapper();

    @Test
    void mapFromCreateBookingCommand() {
        final var spyDomainMapper = spy(subject);
        final var passenger = new Passenger("Edi", "J", "1");

        doReturn(passenger).when(spyDomainMapper).mapToPassenger(any(PassengerItem.class));
        Seat seat = new Seat(1, "A", true);
        doReturn(seat).when(spyDomainMapper).mapToSeat(any());
        doReturn(new ScheduledFlight()).when(spyDomainMapper).mapToScheduledFlight(any());

        final var command = new CreateBookingCommand();
        command.setPassenger(new PassengerItem());

        final var scheduledFlight = mock(ScheduledFlight.class);
        final var booking = spyDomainMapper.mapFromCreateBookingCommand(command, scheduledFlight, seat);

        assertNotNull(booking);
        assertEquals(passenger, booking.getPassenger());
        assertEquals(seat, booking.getAssignedSeat());
        assertEquals(scheduledFlight, booking.getScheduledFlight());

        verify(spyDomainMapper).mapToPassenger(any(PassengerItem.class));
    }

    @Test
    void mapFromCreateFlightCommand() {
        final var command = new CreateFlightCommand();
        command.setCompany(COMPANY);
        command.setNumber(F_123);

        final var flight = subject.mapFromCreateFlightCommand(command);

        assertNotNull(flight);
        assertEquals(COMPANY, flight.getCompany());
        assertEquals(F_123, flight.getNumber());
    }

    @Test
    void mapFromCreateAirplaneCommand() {

        final var spyDomainMapper = spy(DomainMapper.class);

        final var command = new CreateAirplaneCommand();
        command.setIdNumber(A_123);
        command.setModel(CARGO);
        command.setSeatingChart(new SeatingChartDto());

        final var mockSeatingChart = new SeatingChart(0, 0);

        doReturn(mockSeatingChart).when(spyDomainMapper).mapToSeatingChart(any(SeatingChartDto.class));

        final var airplane = spyDomainMapper.mapFromCreateAirplaneCommand(command);

        assertNotNull(airplane);
        assertEquals(A_123, airplane.getIdNumber());
        assertEquals(CARGO, airplane.getModel());
        assertEquals(new SeatingChart(0, 0), airplane.getSeatingChart());
    }

    @Test
    void mapToFindFlightCommand() {

        final var command = subject.mapToFindFlightCommand(F_123);
        assertNotNull(command);
        assertEquals(command.getNumber(), F_123);
    }

    @Test
    void mapFromCreateScheduledFlightCommand() {

        final var command = new CreateScheduledFlightCommand();
        command.setFlightId(F_123);
        command.setAirplane(A_123);
        command.setArrival(ARRIVAL_TIME);
        command.setDeparture(DEPARTURE_TIME);

        final var subject = new DomainMapper();
        final var mockFlight = new Flight(F_123);
        final var mockAirplane = new Airplane(A_123);

        final var scheduledFlight = subject.mapFromCreateScheduledFlightCommand(command, mockFlight, mockAirplane);

        assertNotNull(scheduledFlight);
        assertEquals(mockFlight, scheduledFlight.getFlight());
        assertEquals(scheduledFlight.getBookings(), new LinkedHashMap<>());
        assertEquals(mockAirplane, scheduledFlight.getAirplane());
        assertEquals(LocalDateTime.parse(ARRIVAL_TIME), scheduledFlight.getArrivalTime());
        assertEquals(LocalDateTime.parse(DEPARTURE_TIME), scheduledFlight.getDepartureTime());
        assertNotNull(scheduledFlight.getBookings());
        assertTrue(scheduledFlight.getBookings().isEmpty());
    }

    @Test
    void mapToFindAirplaneCommand() {
        final var command = subject.mapToFindAirplaneCommand(A_123);
        assertNotNull(command);
        assertEquals(command.getNumber(), A_123);
    }

    @Test
    void mapToFindScheduledFlightCommand() {

        final var command = subject.mapToFindScheduledFlightCommand(F_123, LocalDate.parse(DEPARTURE_DATE));
        assertNotNull(command);
        assertEquals(command.getNumber(), F_123);
        assertEquals(command.getDepartureDate(), DEPARTURE_DATE);
    }

    @Test
    void mapToCreateFlightCommand() {
        final var flight = new Flight(F_123);
        flight.setCompany(COMPANY1);

        final var command = subject.mapToCreateFlightCommand(flight);

        assertNotNull(command);
        assertEquals(F_123, command.getNumber());
        assertEquals(COMPANY1, command.getCompany());
    }

    @Test
    void mapToCreateBookingCommand() {
        final var spyDomainMapper = spy(DomainMapper.class);

        final var passengerItem = new PassengerItem();
        passengerItem.setFirstName(FIRST_NAME);
        passengerItem.setLastName(LAST_NAME);
        passengerItem.setIdDocument(ID_DOCUMENT);

        doReturn(passengerItem).when(spyDomainMapper).mapToPassengerDto(any(Passenger.class));

        final var booking = new Booking();
        booking.setPassenger(new Passenger(FIRST_NAME, LAST_NAME, ID_DOCUMENT));
        booking.setAssignedSeat(new Seat(1, "A", true));
        final var scheduledFlight = new ScheduledFlight();
        final var flight = new Flight(F_123);
        scheduledFlight.setFlight(flight);
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-12-12T10:00:00"));
        booking.setScheduledFlight(scheduledFlight);

        final var response = spyDomainMapper.mapToCreateBookingCommand(booking);
        assertNotNull(response);
        assertEquals(passengerItem, response.getPassenger());
        assertEquals("2022-12-12", response.getDepartureDate());
        assertEquals(1, response.getSeatRow());
        assertEquals("A", response.getSeatName());
        assertEquals(F_123, response.getFlightId());
    }

    @Test
    void mapToCreateScheduledFlightCommand() {

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(F_123));
        scheduledFlight.setAirplane(new Airplane(A_123));
        scheduledFlight.setArrivalTime(LocalDateTime.parse("2022-02-20T12:00:00"));
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-02-20T10:00:00"));

        final var command = subject.mapToCreateScheduledFlightCommand(scheduledFlight);
        assertNotNull(command);
        assertEquals(F_123, command.getFlightId());
        assertEquals(A_123, command.getAirplane());
        assertEquals("2022-02-20T12:00", command.getArrival());
        assertEquals("2022-02-20T10:00", command.getDeparture());
    }

    @Test
    void mapToCreateAirplaneCommand() {
        final var spyDomainMapper = spy(DomainMapper.class);
        final var airplane = new Airplane(A_123);
        airplane.setModel(CARGO);
        airplane.setSeatingChart(new SeatingChart(0, 0));

        doReturn(new SeatingChartDto()).when(spyDomainMapper).mapToSeatingChartDto(any(SeatingChart.class));

        final var command = subject.mapToCreateAirplaneCommand(airplane);
        assertNotNull(command);
        assertEquals(A_123, command.getIdNumber());
        assertEquals(CARGO, command.getModel());
        assertInstanceOf(SeatingChartDto.class, command.getSeatingChart());
    }

    @Test
    void mapToFlightItem() {
        final var flight = new Flight(F_123);
        flight.setCompany(COMPANY1);

        final var flightItem = subject.mapToFlightItem(flight);
        assertNotNull(flightItem);
        assertEquals(F_123, flightItem.getNumber());
        assertEquals(COMPANY1, flightItem.getCompany());
    }

    @Test
    void mapToScheduledFlightItem() {
        final var spyDomainMapper = spy(subject);
        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(new Flight(F_123));
        scheduledFlight.setAirplane(new Airplane(A_123));
        scheduledFlight.setArrivalTime(LocalDateTime.parse("2022-02-20T12:00:00"));
        scheduledFlight.setDepartureTime(LocalDateTime.parse("2022-02-20T10:00:00"));

        final var flightItem = new FlightItem();
        flightItem.setNumber(F_123);

        final var airplaneItem = new AirplaneItem();
        airplaneItem.setIdNumber(A_123);
        airplaneItem.setSeatingChart(new SeatingChartDto());
        doReturn(flightItem).when(spyDomainMapper).mapToFlightItem(any(Flight.class));
        doReturn(airplaneItem).when(spyDomainMapper).mapToAirplaneItem(any(Airplane.class));

        final var scheduledFlightItem = spyDomainMapper.mapToScheduledFlightItem(scheduledFlight);

        assertNotNull(scheduledFlightItem);
        assertEquals(flightItem, scheduledFlightItem.getFlight());
        assertEquals(airplaneItem, scheduledFlightItem.getAirplane());
        assertEquals("2022-02-20T12:00", scheduledFlightItem.getArrivalTime());
        assertEquals("2022-02-20T10:00", scheduledFlightItem.getDepartureTime());
    }

    @Test
    void mapToAirplaneItem() {
        final var spyDomainMapper = spy(subject);
        final var airplane = new Airplane(A_123);
        airplane.setModel(CARGO);
        airplane.setSeatingChart(new SeatingChart(0, 0));
        final var mockSeat = new SeatingChartDto();

        doReturn(mockSeat).when(spyDomainMapper).mapToSeatingChartDto(any(SeatingChart.class));
        final var airplaneItem = spyDomainMapper.mapToAirplaneItem(airplane);

        assertNotNull(airplaneItem);
        assertEquals(airplane.getModel(), airplaneItem.getModel());
        assertEquals(airplane.getIdNumber(), airplaneItem.getIdNumber());
        assertEquals(new SeatingChartDto(), airplaneItem.getSeatingChart());
    }

    @Test
    void mapToSeatingChartDto() {
        final var spyDomainMapper = spy(subject);

        SeatItem toBeReturned = new SeatItem();
        toBeReturned.setSeatName("A");
        toBeReturned.setRow(1);
        toBeReturned.setBusinessClass(true);

        doReturn(toBeReturned).when(spyDomainMapper).mapToSeatItem(any(Seat.class));

        final var seatingChart = new SeatingChart(1, 1);
        seatingChart.setSeatsCount(1);
        seatingChart.setSeats(Set.of(new Seat(1, "A", true)));

        final var seatingChartItem = spyDomainMapper.mapToSeatingChartDto(seatingChart);

        assertNotNull(seatingChartItem);
        assertEquals(Set.of(toBeReturned), seatingChartItem.getSeats());
    }

    @Test
    void mapToAirplane() {

        final var spyDomainMapper = spy(subject);
        final var airplane = new AirplaneItem();
        airplane.setModel(CARGO);
        airplane.setIdNumber(A_123);
        airplane.setSeatingChart(new SeatingChartDto());
        final var airplaneTest = spyDomainMapper.mapToAirplane(airplane);

        assertNotNull(airplaneTest);
        assertEquals(A_123, airplaneTest.getIdNumber());
        assertEquals(CARGO, airplaneTest.getModel());
        assertEquals(new SeatingChart(0, 0), airplaneTest.getSeatingChart());
    }

    @Test
    void mapToFlight() {
        final var flightItem = new FlightItem();
        flightItem.setNumber(F_123);
        flightItem.setCompany(COMPANY);

        final var flight = subject.mapToFlight(flightItem);

        assertNotNull(flight);
        assertEquals(F_123, flight.getNumber());
        assertEquals(COMPANY, flight.getCompany());
    }

    @Test
    void mapToPassenger() {
        final var passengerItem = new PassengerItem();
        passengerItem.setFirstName(FIRST_NAME);
        passengerItem.setLastName(LAST_NAME);
        passengerItem.setIdDocument(ID_DOCUMENT);

        final var passenger = subject.mapToPassenger(passengerItem);

        assertNotNull(passenger);
        assertEquals(FIRST_NAME, passenger.getFirstName());
        assertEquals(LAST_NAME, passenger.getLastName());
        assertEquals(ID_DOCUMENT, passenger.getIdDocument());
    }

    @Test
    void mapToPassengerDto() {
        final var passenger = new Passenger(FIRST_NAME, LAST_NAME, ID_DOCUMENT);

        final var passengerDto = subject.mapToPassengerDto(passenger);

        assertNotNull(passengerDto);
        assertEquals(FIRST_NAME, passengerDto.getFirstName());
        assertEquals(LAST_NAME, passengerDto.getLastName());
        assertEquals(ID_DOCUMENT, passengerDto.getIdDocument());
    }

    @Test
    void mapToScheduledFlight() {

        final var spyDomainMapper = spy(subject);

        Flight mockFlight = new Flight(F_123);
        FlightItem flight = new FlightItem();
        flight.setNumber(F_123);

        doReturn(mockFlight).when(spyDomainMapper).mapToFlight(any(FlightItem.class));

        AirplaneItem a123 = new AirplaneItem();
        a123.setIdNumber(A_123);
        a123.setSeatingChart(new SeatingChartDto());
        Airplane mockAirplane = new Airplane(A_123);
        doReturn(mockAirplane).when(spyDomainMapper).mapToAirplane(any(AirplaneItem.class));

        ScheduledFlightItem scheduledFlightItem = new ScheduledFlightItem();
        scheduledFlightItem.setFlight(flight);
        scheduledFlightItem.setArrivalTime(ARRIVAL_TIME);
        scheduledFlightItem.setDepartureTime(DEPARTURE_TIME);
        scheduledFlightItem.setAirplane(a123);

        ScheduledFlight scheduledFlight = spyDomainMapper.mapToScheduledFlight(scheduledFlightItem);

        assertNotNull(scheduledFlight);
        assertEquals(mockFlight, scheduledFlight.getFlight());
        assertEquals(LocalDateTime.parse(ARRIVAL_TIME), scheduledFlight.getArrivalTime());
        assertEquals(LocalDateTime.parse(DEPARTURE_TIME), scheduledFlight.getDepartureTime());
        assertEquals(mockAirplane, scheduledFlight.getAirplane());

        verify(spyDomainMapper).mapToFlight(any(FlightItem.class));
        verify(spyDomainMapper).mapToAirplane(any(AirplaneItem.class));
    }

    @Test
    void mapToSeat() {
        final var seatItem = new SeatItem();

        seatItem.setSeatName("A");
        seatItem.setRow(1);
        seatItem.setBusinessClass(true);

        final var seat = subject.mapToSeat(seatItem);
        assertNotNull(seat);
        assertEquals("A", seat.getSeatName());
        assertEquals(1, seat.getRow());
    }

    @Test
    void mapToSeatDto() {
        final var seat = new Seat(1, "A", true);

        final var seatItem = subject.mapToSeatItem(seat);

        assertNotNull(seatItem);
        assertEquals("A", seatItem.getSeatName());
        assertEquals(1, seatItem.getRow());
    }
}
