package ro.eduardismund.flightmgmt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.server.CreateAirplaneCommand;
import ro.eduardismund.flightmgmt.server.CreateAirplaneResponse;
import ro.eduardismund.flightmgmt.server.CreateBookingCommand;
import ro.eduardismund.flightmgmt.server.CreateBookingResponse;
import ro.eduardismund.flightmgmt.server.CreateFlightCommand;
import ro.eduardismund.flightmgmt.server.CreateFlightResponse;
import ro.eduardismund.flightmgmt.server.CreateScheduledFlightCommand;
import ro.eduardismund.flightmgmt.server.CreateScheduledFlightResponse;
import ro.eduardismund.flightmgmt.server.FindAirplaneCommand;
import ro.eduardismund.flightmgmt.server.FindAirplaneCommandResponse;
import ro.eduardismund.flightmgmt.server.FindFlightCommand;
import ro.eduardismund.flightmgmt.server.FindFlightResponse;
import ro.eduardismund.flightmgmt.server.FindScheduledFlightCommand;
import ro.eduardismund.flightmgmt.server.FindScheduledFlightResponse;
import ro.eduardismund.flightmgmt.server.GetAirplanesCommand;
import ro.eduardismund.flightmgmt.server.GetAirplanesResponse;
import ro.eduardismund.flightmgmt.server.GetFlightsCommand;
import ro.eduardismund.flightmgmt.server.GetFlightsResponse;
import ro.eduardismund.flightmgmt.server.GetScheduledFlightsCommand;
import ro.eduardismund.flightmgmt.server.GetScheduledFlightsResponse;
import ro.eduardismund.flightmgmt.server.XmlManager;
import ro.eduardismund.flightmgmt.server.dtos.AirplaneItem;
import ro.eduardismund.flightmgmt.server.dtos.FlightItem;
import ro.eduardismund.flightmgmt.server.dtos.ScheduledFlightItem;
import ro.eduardismund.flightmgmt.server.dtos.SeatingChartDto;

class RemoteFlightManagementServiceTest {
    public static final String F_123 = "F123";
    public static final String A_123 = "A123";
    public static final String DATE = "2022-12-12";
    private DomainMapper domainMapper;
    private RemoteFlightManagementService subject;
    private XmlManager xmlManager;

    @BeforeEach
    void setUp() throws IOException, JAXBException {
        domainMapper = mock(DomainMapper.class);
        final var socket = mock(Socket.class);
        xmlManager = mock(XmlManager.class);

        when(socket.getInputStream()).thenReturn(mock(InputStream.class));
        when(socket.getOutputStream()).thenReturn(mock(OutputStream.class));

        subject = new RemoteFlightManagementService(domainMapper, xmlManager, socket);
    }

    @SneakyThrows
    @Test
    void init_throwsIoException() {
        final var socket = mock(Socket.class);
        final var ioException = new IOException();
        doThrow(ioException).when(socket).getInputStream();

        assertSame(
                ioException,
                assertThrows(
                        IOException.class,
                        () -> subject = new RemoteFlightManagementService(domainMapper, xmlManager, socket)));
    }

    @Test
    @SneakyThrows
    void sendCommand_isSuccessful() {
        var out = mock(PrintWriter.class);
        var inputStream = mock(BufferedReader.class);

        RemoteFlightManagementService spyService = spy(subject);
        spyService.setOut(out);
        spyService.setReader(inputStream);

        final var command = new GetFlightsCommand();
        final var response = "<example>";
        final var expectedResponse = new GetFlightsResponse();

        doNothing().when(xmlManager).marshal(eq(command), any(PrintWriter.class));
        when(inputStream.readLine()).thenReturn(response);
        when(xmlManager.unmarshal(any(StringReader.class))).thenReturn(expectedResponse);

        final var sendCommand = spyService.sendCommand(command);

        assertNotNull(sendCommand);
        assertEquals(expectedResponse, sendCommand);

        verify(xmlManager).marshal(eq(command), any(PrintWriter.class));
        verify(inputStream).readLine();
        verify(out).println();
        verify(out).flush();
        verify(xmlManager).unmarshal(any(StringReader.class));
    }

    @Test
    @SneakyThrows
    void sendCommand_throwsException() {

        var out = mock(PrintWriter.class);
        var inputStream = mock(BufferedReader.class);

        RemoteFlightManagementService spyService = spy(subject);

        spyService.setOut(out);
        spyService.setReader(inputStream);

        final var command = new GetFlightsCommand();

        doThrow(new IllegalArgumentException("test")).when(xmlManager).marshal(eq(command), any(PrintWriter.class));

        assertThrows(IllegalArgumentException.class, () -> spyService.sendCommand(command));
    }

    @Test
    @SneakyThrows
    void sendCommand_throwsIOException() {

        var out = mock(PrintWriter.class);
        var inputStream = mock(BufferedReader.class);

        RemoteFlightManagementService spyService = spy(subject);
        spyService.setOut(out);
        spyService.setReader(inputStream);

        final var command = new GetFlightsCommand();

        doNothing().when(xmlManager).marshal(eq(command), any(PrintWriter.class));
        doThrow(new IOException("test")).when(inputStream).readLine();

        assertThrows(IOException.class, () -> spyService.sendCommand(command));
    }

    @Test
    @SneakyThrows
    void sendCommand_isNull() {
        var out = mock(PrintWriter.class);
        var inputStream = mock(BufferedReader.class);

        RemoteFlightManagementService spyService = spy(subject);
        spyService.setOut(out);
        spyService.setReader(inputStream);

        final var command = new GetFlightsCommand();

        doNothing().when(xmlManager).marshal(eq(command), any(PrintWriter.class));
        when(inputStream.readLine()).thenReturn(null);

        final var sendCommand = spyService.sendCommand(command);

        assertNull(sendCommand);
    }

    @Test
    void createBooking_isSuccessful() throws IOException {
        final var mockBooking = new Booking();

        var mockCommand = new CreateBookingCommand();
        var mockResponse = new CreateBookingResponse();
        mockResponse.setSuccess(true);

        when(domainMapper.mapToCreateBookingCommand(mockBooking)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertDoesNotThrow(() -> spyService.createBooking(mockBooking));

        verify(domainMapper).mapToCreateBookingCommand(mockBooking);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createBooking_isNotSuccessful() throws IOException {
        final var mockBooking = new Booking();

        var mockCommand = new CreateBookingCommand();
        var mockResponse = new CreateBookingResponse();
        mockResponse.setSuccess(false);
        mockResponse.setError(CreateBookingResponse.CbrErrorType.InternalError);

        when(domainMapper.mapToCreateBookingCommand(mockBooking)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.createBooking(mockBooking));

        verify(domainMapper).mapToCreateBookingCommand(mockBooking);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createFlight_isSuccessful() {
        final var mockFlight = new Flight(F_123);

        var mockCommand = new CreateFlightCommand();
        var mockResponse = new CreateFlightResponse();
        mockResponse.setSuccess(true);

        when(domainMapper.mapToCreateFlightCommand(mockFlight)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertDoesNotThrow(() -> spyService.createFlight(mockFlight));

        verify(domainMapper).mapToCreateFlightCommand(mockFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createFlight_isNotSuccessful_AlreadyExists() {
        final var mockFlight = new Flight(F_123);

        var mockResponse = new CreateFlightResponse();
        mockResponse.setSuccess(false);
        mockResponse.setNumber(F_123);
        mockResponse.setError(CreateFlightResponse.CfrErrorType.FlightAlreadyExists);

        var mockCommand = new CreateFlightCommand();
        when(domainMapper.mapToCreateFlightCommand(mockFlight)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(FlightAlreadyExistsException.class, () -> spyService.createFlight(mockFlight));

        verify(domainMapper).mapToCreateFlightCommand(mockFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createFlight_isNotSuccessful_CouldNotCreate() {
        final var mockFlight = new Flight(F_123);

        var mockResponse = new CreateFlightResponse();
        mockResponse.setSuccess(false);
        mockResponse.setNumber(F_123);
        mockResponse.setError(CreateFlightResponse.CfrErrorType.InternalError);

        var mockCommand = new CreateFlightCommand();
        when(domainMapper.mapToCreateFlightCommand(mockFlight)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.createFlight(mockFlight));

        verify(domainMapper).mapToCreateFlightCommand(mockFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createAirplane_isSuccessful() {
        final var mockAirplane = new Airplane(A_123);

        var mockCommand = new CreateAirplaneCommand();
        var mockResponse = new CreateAirplaneResponse();
        mockResponse.setSuccess(true);

        when(domainMapper.mapToCreateAirplaneCommand(mockAirplane)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertDoesNotThrow(() -> spyService.createAirplane(mockAirplane));

        verify(domainMapper).mapToCreateAirplaneCommand(mockAirplane);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createAirplane_isNotSuccessful_AlreadyExists() {
        final var mockAirplane = new Airplane(A_123);

        var mockResponse = new CreateAirplaneResponse();
        mockResponse.setSuccess(false);
        mockResponse.setAirplaneId(A_123);
        mockResponse.setError(CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists);

        var mockCommand = new CreateAirplaneCommand();
        when(domainMapper.mapToCreateAirplaneCommand(mockAirplane)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(AirplaneAlreadyExistsException.class, () -> spyService.createAirplane(mockAirplane));

        verify(domainMapper).mapToCreateAirplaneCommand(mockAirplane);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createAirplane_isNotSuccessful_CouldNotCreate() {
        final var mockAirplane = new Airplane(A_123);

        var mockResponse = new CreateAirplaneResponse();
        mockResponse.setSuccess(false);
        mockResponse.setAirplaneId(A_123);
        mockResponse.setError(CreateAirplaneResponse.CarErrorType.InternalError);

        var mockCommand = new CreateAirplaneCommand();
        when(domainMapper.mapToCreateAirplaneCommand(mockAirplane)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.createAirplane(mockAirplane));

        verify(domainMapper).mapToCreateAirplaneCommand(mockAirplane);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createScheduledFlight_isSuccessful() {
        final var mockScheduledFlight = new ScheduledFlight();

        var mockCommand = new CreateScheduledFlightCommand();
        var mockResponse = new CreateScheduledFlightResponse();
        mockResponse.setSuccess(true);

        when(domainMapper.mapToCreateScheduledFlightCommand(mockScheduledFlight))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertDoesNotThrow(() -> spyService.createScheduledFlight(mockScheduledFlight));

        verify(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createScheduledFlight_isNotSuccessful_AirplaneAlreadyScheduled() {
        final var mockScheduledFlight = new ScheduledFlight();

        var mockResponse = new CreateScheduledFlightResponse();
        mockResponse.setSuccess(false);
        mockResponse.setAirplaneId(A_123);

        mockResponse.setDepartureTime("12:00:00");
        mockResponse.setArrivalTime("14:00:00");

        mockResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException);

        var mockCommand = new CreateScheduledFlightCommand();
        when(domainMapper.mapToCreateScheduledFlightCommand(mockScheduledFlight))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(
                AirplaneAlreadyScheduledException.class, () -> spyService.createScheduledFlight(mockScheduledFlight));

        verify(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createScheduledFlight_isNotSuccessful_ArrivalBeforeDeparture() {
        final var mockScheduledFlight = new ScheduledFlight();

        var mockResponse = new CreateScheduledFlightResponse();
        mockResponse.setSuccess(false);
        mockResponse.setAirplaneId(A_123);

        mockResponse.setDepartureTime("12:00:00");
        mockResponse.setArrivalTime("11:00:00");

        mockResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException);

        var mockCommand = new CreateScheduledFlightCommand();
        when(domainMapper.mapToCreateScheduledFlightCommand(mockScheduledFlight))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(
                ArrivalBeforeDepartureException.class, () -> spyService.createScheduledFlight(mockScheduledFlight));

        verify(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createScheduledFlight_isNotSuccessful_ScheduledFlightAlreadyExists() {
        final var mockScheduledFlight = new ScheduledFlight();

        var mockResponse = new CreateScheduledFlightResponse();
        mockResponse.setSuccess(false);
        mockResponse.setFlightId(F_123);

        mockResponse.setDepartureTime(DATE);

        mockResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException);

        var mockCommand = new CreateScheduledFlightCommand();
        when(domainMapper.mapToCreateScheduledFlightCommand(mockScheduledFlight))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(
                ScheduledFlightAlreadyExistsException.class,
                () -> spyService.createScheduledFlight(mockScheduledFlight));

        verify(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void createScheduledFlight_isNotSuccessful_CouldNotCreate() {
        final var mockScheduledFlight = new ScheduledFlight();

        var mockCommand = new CreateScheduledFlightCommand();
        var mockResponse = new CreateScheduledFlightResponse();
        mockResponse.setSuccess(false);

        mockResponse.setError(CreateScheduledFlightResponse.CsfrErrorType.InternalError);

        when(domainMapper.mapToCreateScheduledFlightCommand(mockScheduledFlight))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.createScheduledFlight(mockScheduledFlight));

        verify(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void findFlight_isFound() {

        var mockCommand = new FindFlightCommand();
        var mockResponse = new FindFlightResponse();
        mockCommand.setNumber(F_123);
        mockResponse.setSuccess(true);
        FlightItem flight = new FlightItem();
        flight.setNumber(F_123);
        mockResponse.setFlight(flight);

        final var testFlight = new Flight(F_123);

        when(domainMapper.mapToFindFlightCommand(F_123)).thenReturn(mockCommand);
        when(domainMapper.mapToFlight(flight)).thenReturn(testFlight);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        final var foundFlight = spyService.findFlight(F_123);
        assertTrue(foundFlight.isPresent());
        assertEquals(foundFlight.get(), testFlight);

        verify(domainMapper).mapToFindFlightCommand(F_123);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void findFlight_isNotFound() {

        var mockCommand = new FindFlightCommand();
        var mockResponse = new FindFlightResponse();
        mockCommand.setNumber(F_123);
        mockResponse.setSuccess(false);
        mockResponse.setFlight(null);

        when(domainMapper.mapToFindFlightCommand(F_123)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.findFlight(F_123));

        verify(domainMapper).mapToFindFlightCommand(F_123);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void findAirplane_isFound() {
        AirplaneItem airplane = new AirplaneItem();
        airplane.setIdNumber(A_123);
        airplane.setModel("Boeing 747");
        airplane.setSeatingChart(new SeatingChartDto());

        var mockCommand = new FindAirplaneCommand();
        var mockResponse = new FindAirplaneCommandResponse();
        mockCommand.setNumber(A_123);
        mockResponse.setFound(true);

        mockResponse.setAirplaneItem(airplane);

        when(domainMapper.mapToFindAirplaneCommand(A_123)).thenReturn(mockCommand);
        Airplane a123 = new Airplane(A_123);
        when(domainMapper.mapToAirplane(airplane)).thenReturn(a123);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        final var airplane1 = spyService.findAirplane(A_123);
        assertTrue(airplane1.isPresent());
        assertEquals(airplane1.get(), a123);

        verify(domainMapper).mapToFindAirplaneCommand(A_123);
        verify(domainMapper).mapToAirplane(airplane);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void findAirplane_isNotFound() {

        var mockCommand = new FindAirplaneCommand();
        var mockResponse = new FindAirplaneCommandResponse();
        mockCommand.setNumber(A_123);
        mockResponse.setFound(false);
        mockResponse.setAirplaneItem(null);

        when(domainMapper.mapToFindAirplaneCommand(A_123)).thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.findAirplane(A_123));

        verify(domainMapper).mapToFindAirplaneCommand(A_123);
        verify(spyService).sendCommand(mockCommand);
    }

    @Test
    void getFlights_isSuccessful() {
        var mockResponse = new GetFlightsResponse();
        mockResponse.setSuccess(true);
        var flight = new FlightItem();
        flight.setNumber(F_123);
        mockResponse.setFlights(List.of(flight));

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetFlightsCommand.class));

        final var flights = assertDoesNotThrow(spyService::getFlights);

        verify(spyService).sendCommand(any(GetFlightsCommand.class));
        assertNotNull(flights);
        assertEquals(flights.size(), 1);
    }

    @Test
    void getFlights_isNotSuccessful() {
        var mockResponse = new GetFlightsResponse();
        mockResponse.setSuccess(false);
        mockResponse.setFlights(null);

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetFlightsCommand.class));

        assertThrows(IllegalStateException.class, spyService::getFlights);

        verify(spyService).sendCommand(any(GetFlightsCommand.class));
    }

    @Test
    void getAirplanes_isSuccessful() {
        var mockResponse = new GetAirplanesResponse();
        mockResponse.setSuccess(true);
        var airplane = new AirplaneItem();
        airplane.setModel(A_123);
        airplane.setSeatingChart(new SeatingChartDto());
        mockResponse.setAirplanes(List.of(airplane));

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetAirplanesCommand.class));

        final var airplanes = assertDoesNotThrow(spyService::getAirplanes);

        verify(spyService).sendCommand(any(GetAirplanesCommand.class));
        assertNotNull(airplanes);
        assertEquals(airplanes.size(), 1);
    }

    @Test
    void getAirplanes_isNotSuccessful() {
        var mockResponse = new GetAirplanesResponse();
        mockResponse.setSuccess(false);
        mockResponse.setAirplanes(null);

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetAirplanesCommand.class));

        assertThrows(IllegalStateException.class, spyService::getAirplanes);

        verify(spyService).sendCommand(any(GetAirplanesCommand.class));
    }

    @Test
    void getScheduledFlights_isSuccessful() {
        var mockResponse = new GetScheduledFlightsResponse();
        mockResponse.setSuccess(true);
        var scheduledFlight = new ScheduledFlightItem();
        var flight = new FlightItem();
        var airplane = new AirplaneItem();
        flight.setNumber(F_123);
        airplane.setIdNumber(A_123);
        scheduledFlight.setFlight(flight);
        scheduledFlight.setAirplane(airplane);

        mockResponse.setScheduledFlights(List.of(scheduledFlight));

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetScheduledFlightsCommand.class));

        final var scheduledFlights = assertDoesNotThrow(spyService::getScheduledFlights);

        verify(spyService).sendCommand(any(GetScheduledFlightsCommand.class));
        assertNotNull(scheduledFlights);
        assertEquals(scheduledFlights.size(), 1);
    }

    @Test
    void getScheduledFlights_isNotSuccessful() {
        var mockResponse = new GetScheduledFlightsResponse();
        mockResponse.setSuccess(false);
        mockResponse.setScheduledFlights(null);

        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(any(GetScheduledFlightsCommand.class));

        assertThrows(IllegalStateException.class, spyService::getScheduledFlights);

        verify(spyService).sendCommand(any(GetScheduledFlightsCommand.class));
    }

    @Test
    void findScheduledFlights_isFound() {

        var mockCommand = new FindScheduledFlightCommand();
        mockCommand.setNumber(F_123);
        mockCommand.setDepartureDate(DATE);

        var scheduledFlight = new ScheduledFlightItem();

        scheduledFlight.setFlight(new FlightItem());
        scheduledFlight.setAirplane(new AirplaneItem());

        var mockResponse = new FindScheduledFlightResponse();
        mockResponse.setScheduledFlightItem(scheduledFlight);
        mockResponse.setFound(true);

        when(domainMapper.mapToScheduledFlight(scheduledFlight)).thenReturn(new ScheduledFlight());

        when(domainMapper.mapToFindScheduledFlightCommand(F_123, LocalDate.parse(DATE)))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        final var flight = spyService.findScheduledFlight(F_123, LocalDate.parse(DATE));
        assertTrue(flight.isPresent());
        assertEquals(flight.get(), new ScheduledFlight());

        verify(domainMapper).mapToFindScheduledFlightCommand(F_123, LocalDate.parse(DATE));
        verify(spyService).sendCommand(mockCommand);
        verify(domainMapper).mapToScheduledFlight(scheduledFlight);

        var resultSf = spyService.findScheduledFlight(F_123, LocalDate.parse(DATE));
        assertNotNull(resultSf);
    }

    @Test
    void findScheduledFlight_isNotFound() {

        var mockCommand = new FindScheduledFlightCommand();
        var mockResponse = new FindScheduledFlightResponse();
        mockResponse.setFound(false);
        mockResponse.setScheduledFlightItem(null);

        when(domainMapper.mapToFindScheduledFlightCommand(F_123, LocalDate.parse(DATE)))
                .thenReturn(mockCommand);
        RemoteFlightManagementService spyService = spy(subject);
        doReturn(mockResponse).when(spyService).sendCommand(mockCommand);

        assertThrows(IllegalStateException.class, () -> spyService.findScheduledFlight(F_123, LocalDate.parse(DATE)));

        verify(domainMapper).mapToFindScheduledFlightCommand(F_123, LocalDate.parse(DATE));
        verify(spyService).sendCommand(mockCommand);
    }
}
