package ro.eduardismund.flightmgmt.service;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.*;

/**
 *  Http client implementation of the {@link FlightManagementService}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
@RequiredArgsConstructor
public class HttpClientFlightMgmtService implements FlightManagementService {
    private final HttpClient httpClient;
    private final URI baseUri;
    private final XmlManager xmlManager;
    private final DomainMapper domainMapper;

    @Override
    public void createBooking(Booking booking) {
        sendPostRequest(domainMapper.mapToCreateBookingCommand(booking), "bookings", CreateBookingResponse.class);
    }

    @Override
    public void createFlight(Flight flight) throws FlightAlreadyExistsException {

        final var response =
                sendPostRequest(domainMapper.mapToCreateFlightCommand(flight), "flights", CreateFlightResponse.class);
        final var flightAlreadyExists = Optional.ofNullable(response)
                .map(CreateFlightResponse::getError)
                .filter(Predicate.isEqual(CreateFlightResponse.CfrErrorType.FlightAlreadyExists))
                .isPresent();

        if (flightAlreadyExists) {
            throw new FlightAlreadyExistsException(flight.getNumber());
        }
    }

    @SneakyThrows
    <T> T sendPostRequest(Object objectRequest, String resource, Class<T> clazz) {
        final var writer = new StringWriter();
        try (var printWriter = getPrintWriter(writer)) {
            xmlManager.marshal(objectRequest, printWriter);
        }

        final var uri = baseUri + "/" + resource;
        final var request = createPostHttpRequest(uri, writer);
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HttpURLConnection.HTTP_CREATED) {
            return null;
        }

        return Optional.of(response.statusCode())
                .filter(Predicate.isEqual(400))
                .flatMap(rss -> response.headers().firstValue("content-type"))
                .filter(HttpClientFlightMgmtService::isXml)
                .map(contentType -> response.body())
                .map(StringReader::new)
                .map(xmlManager::unmarshal)
                .map(clazz::cast)
                .orElseThrow(() -> new IllegalStateException(uri + " returned " + response.statusCode()));
    }

    static boolean isXml(String contentType) {
        return contentType.endsWith("xml");
    }

    HttpRequest createPostHttpRequest(String uri, StringWriter writer) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(writer.toString()))
                .build();
    }

    PrintWriter getPrintWriter(StringWriter writer) {
        return new PrintWriter(writer);
    }

    @Override
    public void createAirplane(Airplane airplane) throws AirplaneAlreadyExistsException {

        final var response = sendPostRequest(
                domainMapper.mapToCreateAirplaneCommand(airplane), "airplanes", CreateAirplaneResponse.class);
        final var airplaneExists = Optional.ofNullable(response)
                .map(CreateAirplaneResponse::getError)
                .filter(Predicate.isEqual(CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists))
                .isPresent();

        if (airplaneExists) {
            throw new AirplaneAlreadyExistsException(airplane.getIdNumber());
        }
    }

    @SuppressWarnings({"PMD.NonExhaustiveSwitch", "checkstyle:MissingSwitchDefault"})
    @Override
    public void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException {

        final var response = sendPostRequest(
                domainMapper.mapToCreateScheduledFlightCommand(scheduledFlight),
                "scheduled-flights",
                CreateScheduledFlightResponse.class);
        if (response == null) {
            return;
        }

        final var scheduledFlightError = response.getError();

        switch (scheduledFlightError) {
            case ScheduledFlightAlreadyExistsException -> throw new ScheduledFlightAlreadyExistsException(
                    scheduledFlight.getFlight().getNumber(),
                    scheduledFlight.getDepartureTime().toLocalDate());
            case AirplaneAlreadyScheduledException -> throw new AirplaneAlreadyScheduledException(
                    scheduledFlight.getFlight().getNumber(),
                    scheduledFlight.getDepartureTime().toLocalTime(),
                    scheduledFlight.getArrivalTime().toLocalTime());
            case ArrivalBeforeDepartureException -> throw new ArrivalBeforeDepartureException(
                    scheduledFlight.getDepartureTime().toLocalTime(),
                    scheduledFlight.getArrivalTime().toLocalTime());
        }
    }

    @Override
    public Optional<Flight> findFlight(String number) {
        return Optional.of(sendGetRequest(FindFlightResponse.class, "flights", number)
                        .getFlight())
                .map(domainMapper::mapToFlight);
    }

    @Override
    public Optional<Airplane> findAirplane(String idNumber) {
        return Optional.of(sendGetRequest(FindAirplaneCommandResponse.class, "airplanes", idNumber)
                        .getAirplaneItem())
                .map(domainMapper::mapToAirplane);
    }

    <T> T sendGetRequest(Class<T> clazz, String... resource) {
        return sendGetRequest(clazz, resource, Map.of());
    }

    @SneakyThrows
    <T> T sendGetRequest(Class<T> clazz, String[] resource, Map<String, String> queryParams) {
        var uri = URI.create(Stream.concat(Stream.of(baseUri.toString()), Arrays.stream(resource))
                .collect(Collectors.joining("/")));
        uri = setQueryParams(uri, queryParams);
        final var request = HttpRequest.newBuilder().uri(uri).build();
        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            return clazz.cast(xmlManager.unmarshal(new StringReader(response.body())));
        }
        throw new IllegalStateException(uri + " returned " + response.statusCode());
    }

    @Override
    public List<Flight> getFlights() {
        return sendGetRequest(GetFlightsResponse.class, "flights").getFlights().stream()
                .map(domainMapper::mapToFlight)
                .toList();
    }

    @Override
    public List<Airplane> getAirplanes() {
        return sendGetRequest(GetAirplanesResponse.class, "airplanes").getAirplanes().stream()
                .map(domainMapper::mapToAirplane)
                .toList();
    }

    @Override
    public List<ScheduledFlight> getScheduledFlights() {
        return sendGetRequest(GetScheduledFlightsResponse.class, "scheduled-flights").getScheduledFlights().stream()
                .map(domainMapper::mapToScheduledFlight)
                .toList();
    }

    @Override
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
        return Optional.of(sendGetRequest(
                                FindScheduledFlightResponse.class,
                                new String[] {"scheduled-flights"},
                                Map.of("flight-id", flightNumber, "departure-date", localDate.toString()))
                        .getScheduledFlightItem())
                .map(domainMapper::mapToScheduledFlight);
    }

    static URI setQueryParams(URI baseUri, Map<String, String> queryParams) {

        if (queryParams.isEmpty()) {
            return baseUri;
        }

        final var queryString = queryParams.entrySet().stream()
                .map(HttpClientFlightMgmtService::encodeQueryStringEntry)
                .collect(Collectors.joining("&"));

        return URI.create(baseUri.toString() + "?" + queryString);
    }

    private static String encodeQueryStringEntry(Map.Entry<String, String> entry) {
        return String.format("%s=%s", encode(entry.getKey()), encode(entry.getValue()));
    }

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }
}
