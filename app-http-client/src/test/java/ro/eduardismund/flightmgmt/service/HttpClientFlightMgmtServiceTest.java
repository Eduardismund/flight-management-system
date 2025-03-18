package ro.eduardismund.flightmgmt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.*;

class HttpClientFlightMgmtServiceTest {
    public static final String FLIGHTS = "flights";
    public static final String F_123 = "F123";
    public static final String A_123 = "A123";
    public static final String SCHEDULED_FLIGHTS = "scheduled-flights";
    public static final String AIRPLANES = "airplanes";
    private HttpClient httpClient;
    private URI baseUri;
    private XmlManager xmlManager;
    private DomainMapper domainMapper;
    private HttpClientFlightMgmtService subject;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        baseUri = mock(URI.class);
        xmlManager = mock(XmlManager.class);
        domainMapper = mock(DomainMapper.class);
        subject = spy(new HttpClientFlightMgmtService(httpClient, baseUri, xmlManager, domainMapper));
    }

    @Test
    void sendPostRequest_isSuccessful() throws IOException, InterruptedException {
        final var objectRequest = mock(Object.class);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(subject).getPrintWriter(any(StringWriter.class));
        doReturn("http://base-uri").when(baseUri).toString();

        final var mockRequest = mock(HttpRequest.class);
        doReturn(mockRequest)
                .when(subject)
                .createPostHttpRequest(eq("http://base-uri/flights"), any(StringWriter.class));

        final var mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(httpClient).send(mockRequest, HttpResponse.BodyHandlers.ofString());

        doReturn(201).when(mockResponse).statusCode();

        assertNull(subject.sendPostRequest(objectRequest, FLIGHTS, CreateFlightResponse.class));

        verify(xmlManager).marshal(objectRequest, writer);
        verify(mockResponse).statusCode();
        verifyNoMoreInteractions(mockResponse);
        verify(writer).close();
    }

    @Test
    void sendPostRequest_isSuccessful1() throws IOException, InterruptedException {
        final var objectRequest = mock(Object.class);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(subject).getPrintWriter(any(StringWriter.class));
        doReturn("http://base-uri").when(baseUri).toString();

        final var mockRequest = mock(HttpRequest.class);
        doReturn(mockRequest)
                .when(subject)
                .createPostHttpRequest(eq("http://base-uri/flights"), any(StringWriter.class));

        final var mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(httpClient).send(mockRequest, HttpResponse.BodyHandlers.ofString());

        doReturn(400).when(mockResponse).statusCode();

        HttpHeaders headers = HttpHeaders.of(Map.of("content-type", List.of("1/xml")), (name, value) -> true);
        doReturn(headers).when(mockResponse).headers();
        final var responseBody = "</>";
        doReturn(responseBody).when(mockResponse).body();
        final var responseObject = mock(CreateFlightResponse.class);
        final var captor = ArgumentCaptor.forClass(StringReader.class);
        doReturn(responseObject).when(xmlManager).unmarshal(captor.capture());
        assertInstanceOf(
                CreateFlightResponse.class,
                subject.sendPostRequest(objectRequest, FLIGHTS, CreateFlightResponse.class));

        verify(xmlManager).marshal(objectRequest, writer);
        verify(writer).close();
        assertEquals(responseBody, new BufferedReader(captor.getValue()).lines().collect(Collectors.joining("\n")));
    }

    @Test
    void sendPostRequest_throws() throws IOException, InterruptedException {
        final var objectRequest = mock(Object.class);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(subject).getPrintWriter(any(StringWriter.class));
        doReturn("http://base-uri").when(baseUri).toString();

        final var mockRequest = mock(HttpRequest.class);
        doReturn(mockRequest)
                .when(subject)
                .createPostHttpRequest(eq("http://base-uri/flights"), any(StringWriter.class));

        final var mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(httpClient).send(mockRequest, HttpResponse.BodyHandlers.ofString());

        doReturn(399).when(mockResponse).statusCode();

        assertEquals(
                "http://base-uri/flights returned 399",
                assertThrows(
                                IllegalStateException.class,
                                () -> subject.sendPostRequest(objectRequest, FLIGHTS, CreateFlightResponse.class))
                        .getMessage());

        verify(xmlManager).marshal(objectRequest, writer);
        verify(writer).close();
    }

    @ParameterizedTest
    @CsvSource({"application/xml,true", "application/json,false"})
    void isXml(String contentType, boolean isXml) {
        assertEquals(isXml, HttpClientFlightMgmtService.isXml(contentType));
    }

    @Test
    void createBooking() {
        final var mockBooking = mock(Booking.class);
        final var mockCreateCommand = mock(CreateBookingCommand.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateBookingCommand(mockBooking);
        doReturn(null).when(subject).sendPostRequest(mockCreateCommand, "bookings", CreateBookingResponse.class);

        assertDoesNotThrow(() -> subject.createBooking(mockBooking));
    }

    @Test
    void createFlight_isSuccessful() {

        final var mockFlight = mock(Flight.class);
        final var mockCreateCommand = mock(CreateFlightCommand.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateFlightCommand(mockFlight);
        doReturn(null).when(subject).sendPostRequest(mockCreateCommand, FLIGHTS, CreateFlightResponse.class);

        assertDoesNotThrow(() -> subject.createFlight(mockFlight));
    }

    @ParameterizedTest
    @CsvSource({
        "ScheduledFlightAlreadyExistsException",
        "AirplaneAlreadyScheduledException",
        "ArrivalBeforeDepartureException",
        "InternalError"
    })
    void createScheduledFlight_isNotSuccessful(CreateScheduledFlightResponse.CsfrErrorType exceptionType) {

        final var mockScheduledFlight = mock(ScheduledFlight.class);
        final var mockFlight = mock(Flight.class);
        doReturn(mockFlight).when(mockScheduledFlight).getFlight();
        doReturn(F_123).when(mockFlight).getNumber();
        doReturn(LocalDateTime.parse("2022-12-12T12:00:00"))
                .when(mockScheduledFlight)
                .getDepartureTime();
        doReturn(LocalDateTime.parse("2022-12-12T13:00:00"))
                .when(mockScheduledFlight)
                .getArrivalTime();
        final var mockCreateCommand = mock(CreateScheduledFlightCommand.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        final var mockResponse = mock(CreateScheduledFlightResponse.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        doReturn(mockResponse)
                .when(subject)
                .sendPostRequest(mockCreateCommand, SCHEDULED_FLIGHTS, CreateScheduledFlightResponse.class);

        doReturn(exceptionType).when(mockResponse).getError();

        switch (exceptionType) {
            case ScheduledFlightAlreadyExistsException:
                var exception1 = assertThrows(
                        ScheduledFlightAlreadyExistsException.class,
                        () -> subject.createScheduledFlight(mockScheduledFlight));
                assertContains(exception1.getMessage(), F_123, "2022-12-12");
                break;
            case AirplaneAlreadyScheduledException:
                var exception2 = assertThrows(
                        AirplaneAlreadyScheduledException.class,
                        () -> subject.createScheduledFlight(mockScheduledFlight));
                assertContains(exception2.getMessage(), F_123, "12:00", "13:00");
                break;
            case ArrivalBeforeDepartureException:
                var exception3 = assertThrows(
                        ArrivalBeforeDepartureException.class,
                        () -> subject.createScheduledFlight(mockScheduledFlight));
                assertContains(exception3.getMessage(), "12:00", "13:00");
                break;
            case InternalError:
                assertDoesNotThrow(() -> subject.createScheduledFlight(mockScheduledFlight));
        }
    }

    private static void assertContains(String actual, String... expectedParts) {
        Arrays.stream(expectedParts)
                .forEach((part) ->
                        assertTrue(actual.contains(part), "Expected <" + actual + "> to contain  <" + part + ">"));
    }

    @Test
    void createScheduledFlight_isSuccessful() {

        final var mockScheduledFlight = mock(ScheduledFlight.class);
        final var mockCreateCommand = mock(CreateScheduledFlightCommand.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateScheduledFlightCommand(mockScheduledFlight);
        doReturn(null)
                .when(subject)
                .sendPostRequest(mockCreateCommand, SCHEDULED_FLIGHTS, CreateScheduledFlightResponse.class);

        assertDoesNotThrow(() -> subject.createScheduledFlight(mockScheduledFlight));
    }

    @Test
    void createAirplane_isNotSuccessful() {

        final var mockAirplane = mock(Airplane.class);
        doReturn(A_123).when(mockAirplane).getIdNumber();
        final var mockCreateCommand = mock(CreateAirplaneCommand.class);
        final var mockResponse = mock(CreateAirplaneResponse.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateAirplaneCommand(mockAirplane);
        doReturn(mockResponse)
                .when(subject)
                .sendPostRequest(mockCreateCommand, AIRPLANES, CreateAirplaneResponse.class);

        doReturn(CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists)
                .when(mockResponse)
                .getError();

        assertContains(
                assertThrows(AirplaneAlreadyExistsException.class, () -> subject.createAirplane(mockAirplane))
                        .getMessage(),
                A_123);
    }

    @Test
    void createAirplane_isSuccessful() {

        final var mockAirplane = mock(Airplane.class);
        final var mockCreateCommand = mock(CreateAirplaneCommand.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateAirplaneCommand(mockAirplane);
        doReturn(null).when(subject).sendPostRequest(mockCreateCommand, AIRPLANES, CreateAirplaneResponse.class);

        assertDoesNotThrow(() -> subject.createAirplane(mockAirplane));
    }

    @Test
    void createFlight_isNotSuccessful() {

        final var mockFlight = mock(Flight.class);
        doReturn(F_123).when(mockFlight).getNumber();
        final var mockCreateCommand = mock(CreateFlightCommand.class);
        final var mockResponse = mock(CreateFlightResponse.class);

        doReturn(mockCreateCommand).when(domainMapper).mapToCreateFlightCommand(mockFlight);
        doReturn(mockResponse).when(subject).sendPostRequest(mockCreateCommand, FLIGHTS, CreateFlightResponse.class);

        doReturn(CreateFlightResponse.CfrErrorType.FlightAlreadyExists)
                .when(mockResponse)
                .getError();

        assertTrue(assertThrows(FlightAlreadyExistsException.class, () -> subject.createFlight(mockFlight))
                .getMessage()
                .contains(F_123));
    }

    @Test
    void findFlight() {
        assertFindEntityItem(
                FindFlightResponse.class,
                Flight.class,
                FlightItem.class,
                FindFlightResponse::getFlight,
                DomainMapper::mapToFlight,
                subject -> subject.findFlight(F_123),
                (path, queryParams) -> {
                    assertEquals(2, path.length);
                    assertEquals(FLIGHTS, path[0]);
                    assertEquals(F_123, path[1]);
                });
    }

    @Test
    void findAirplane() {
        assertFindEntityItem(
                FindAirplaneCommandResponse.class,
                Airplane.class,
                AirplaneItem.class,
                FindAirplaneCommandResponse::getAirplaneItem,
                DomainMapper::mapToAirplane,
                subject -> subject.findAirplane(A_123),
                (path, queryParams) -> {
                    assertEquals(2, path.length);
                    assertEquals(AIRPLANES, path[0]);
                    assertEquals(A_123, path[1]);
                });
    }

    @Test
    void getFlights() {
        assertGetEntityList(
                FLIGHTS,
                GetFlightsResponse.class,
                Flight.class,
                FlightItem.class,
                DomainMapper::mapToFlight,
                GetFlightsResponse::getFlights,
                HttpClientFlightMgmtService::getFlights);
    }

    private <R, D, T> void assertGetEntityList(
            String entityName,
            Class<R> responseClass,
            Class<D> domainClass,
            Class<T> dtoClass,
            BiFunction<DomainMapper, T, D> mapper,
            Function<R, List<T>> getResponseItems,
            Function<HttpClientFlightMgmtService, List<D>> getDomainList) {
        final var response = mock(responseClass);
        final var mockDomain = mock(domainClass);
        final var mockDtoItem = mock(dtoClass);
        final var mockDtoItems = List.of(mockDtoItem);

        final var captorResource = ArgumentCaptor.forClass(String[].class);

        mapper.apply(doReturn(mockDomain).when(domainMapper), mockDtoItem);
        getResponseItems.apply(doReturn(mockDtoItems).when(response));
        doReturn(response).when(subject).sendGetRequest(eq(responseClass), captorResource.capture());

        final var foundAirplanes = getDomainList.apply(subject);
        assertSame(mockDomain, foundAirplanes.getFirst());
        assertEquals(entityName, captorResource.getValue()[0]);
    }

    @Test
    void getAirplanes() {
        assertGetEntityList(
                AIRPLANES,
                GetAirplanesResponse.class,
                Airplane.class,
                AirplaneItem.class,
                DomainMapper::mapToAirplane,
                GetAirplanesResponse::getAirplanes,
                HttpClientFlightMgmtService::getAirplanes);
    }

    @Test
    void getScheduledFlights() {
        assertGetEntityList(
                SCHEDULED_FLIGHTS,
                GetScheduledFlightsResponse.class,
                ScheduledFlight.class,
                ScheduledFlightItem.class,
                DomainMapper::mapToScheduledFlight,
                GetScheduledFlightsResponse::getScheduledFlights,
                HttpClientFlightMgmtService::getScheduledFlights);
    }

    @SuppressWarnings("unchecked")
    private <R, D, T> void assertFindEntityItem(
            Class<R> resClass,
            Class<D> domainItemClass,
            Class<T> dtoItemClass,
            Function<R, T> resToDtoItem,
            BiFunction<DomainMapper, T, D> mapToDomain,
            Function<HttpClientFlightMgmtService, Optional<D>> subjectMethod,
            BiConsumer<String[], Map<String, String>> assertUri) {
        final var response = mock(resClass);
        final var mockDomain = mock(domainItemClass);
        final var mockDtoItem = mock(dtoItemClass);

        final var captorResource = ArgumentCaptor.forClass(String[].class);
        final var captorQueryParams = ArgumentCaptor.forClass(Map.class);

        resToDtoItem.apply(doReturn(mockDtoItem).when(response));

        mapToDomain.apply(doReturn(mockDomain).when(domainMapper), mockDtoItem);
        doReturn(response)
                .when(subject)
                .sendGetRequest(eq(resClass), captorResource.capture(), captorQueryParams.capture());

        final var foundDomain = subjectMethod.apply(subject);
        assertTrue(foundDomain.isPresent());
        assertSame(mockDomain, foundDomain.get());

        assertUri.accept(captorResource.getValue(), captorQueryParams.getValue());
    }

    @Test
    void findScheduledFlight() {
        assertFindEntityItem(
                FindScheduledFlightResponse.class,
                ScheduledFlight.class,
                ScheduledFlightItem.class,
                FindScheduledFlightResponse::getScheduledFlightItem,
                DomainMapper::mapToScheduledFlight,
                subject -> subject.findScheduledFlight(F_123, LocalDate.parse("2022-12-12")),
                (path, queryParams) -> {
                    assertEquals(1, path.length);
                    assertEquals(SCHEDULED_FLIGHTS, path[0]);
                    assertEquals(Set.of("flight-id", "departure-date"), Set.copyOf(queryParams.keySet()));
                    assertEquals(F_123, queryParams.get("flight-id"));
                    assertEquals("2022-12-12", queryParams.get("departure-date"));
                });
    }

    @Test
    void sendGetRequest_isSuccessful() throws IOException, InterruptedException {
        doReturn("http://baseUri").when(baseUri).toString();
        final var mockResponse = mock(HttpResponse.class);
        final var requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        doReturn(mockResponse).when(httpClient).send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString()));
        doReturn(200).when(mockResponse).statusCode();
        final var responseObject = new FindScheduledFlightResponse();
        final var responseBody = "</>";
        doReturn(responseBody).when(mockResponse).body();

        final var responseCaptor = ArgumentCaptor.forClass(StringReader.class);
        doReturn(responseObject).when(xmlManager).unmarshal(responseCaptor.capture());

        Map<String, String> queryParams = new TreeMap<>(Map.of("key 1", "value1", "key2", "value 2"));
        assertSame(
                responseObject,
                subject.sendGetRequest(
                        FindScheduledFlightResponse.class, new String[] {"scheduled", FLIGHTS}, queryParams));

        assertEquals(
                "http://baseUri/scheduled/flights?key+1=value1&key2=value+2",
                requestCaptor.getValue().uri().toString());
        assertEquals(
                responseBody,
                new BufferedReader(responseCaptor.getValue()).lines().collect(Collectors.joining("\n")));
    }

    @Test
    void sendGetRequest_throwsException() throws IOException, InterruptedException {
        doReturn("http://baseUri").when(baseUri).toString();
        final var mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(httpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
        doReturn(199).when(mockResponse).statusCode();

        Map<String, String> queryParams = new TreeMap<>(Map.of("key1", "value1", "key2", "value2"));
        assertEquals(
                "http://baseUri/scheduled/flights?key1=value1&key2=value2 returned 199",
                assertThrows(
                                IllegalStateException.class,
                                () -> subject.sendGetRequest(
                                        FindScheduledFlightResponse.class,
                                        new String[] {"scheduled", FLIGHTS},
                                        queryParams))
                        .getMessage());
    }

    @Test
    void setQueryParams_isEmpty() {
        final var uri = mock(URI.class);
        final var queryParams = Map.<String, String>of();

        assertSame(uri, HttpClientFlightMgmtService.setQueryParams(uri, queryParams));
        verifyNoInteractions(uri);
    }

    @Test
    void getPrintWriter() {
        final var writer = new StringWriter();

        final var printWriter = subject.getPrintWriter(writer);
        printWriter.print("apa");

        assertEquals("apa", writer.toString());
    }

    @Test
    void createPostHttpResponse() {
        final var writer = new StringWriter();
        writer.write("apa");
        final var httpRequest = subject.createPostHttpRequest("http://edi", writer);
        assertEquals("http://edi", httpRequest.uri().toString());
        assertEquals("POST", httpRequest.method());
        assertEquals(Optional.of("application/xml"), httpRequest.headers().firstValue("Content-type"));
        //        assertInstanceOf(Optional.of(HttpRequest.BodyPublishers.ofString("apa")),
        // httpRequest.bodyPublisher());
    }
}
