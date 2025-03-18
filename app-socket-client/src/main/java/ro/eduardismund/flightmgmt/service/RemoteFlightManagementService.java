package ro.eduardismund.flightmgmt.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.dtos.*;

/**
 * Service to manage flights, airplanes, bookings, and scheduled flights via remote server communication.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
@Setter(AccessLevel.PACKAGE)
public class RemoteFlightManagementService implements FlightManagementService {

    private final DomainMapper domainMapper;
    private final XmlManager xmlManager;
    private BufferedReader reader;
    private PrintWriter out;

    /**
     * Initializes the service with necessary components.
     *
     * @param domainMapper The domain mapper to convert objects.
     * @param xmlManager   The XML manager for marshalling and unmarshalling data.
     * @param socket       The socket for server communication.
     */
    @SneakyThrows
    public RemoteFlightManagementService(DomainMapper domainMapper, XmlManager xmlManager, Socket socket) {
        this.domainMapper = domainMapper;
        this.xmlManager = xmlManager;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    /**
     * Creates a booking on the server.
     *
     * @param booking The booking to be created.
     * @throws IllegalStateException If booking creation fails.
     */
    @Override
    public void createBooking(Booking booking) {
        final CreateBookingResponse response = sendCommand(domainMapper.mapToCreateBookingCommand(booking));
        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not create booking!");
        }
    }

    /**
     * Sends a command to the server and receives the response.
     *
     * @param command The command to send.
     * @param <T> The response type.
     * @return The response object.
     */
    @SneakyThrows
    <T> T sendCommand(Object command) {
        xmlManager.marshal(command, out);
        out.println();
        out.flush();
        final var responseText = reader.readLine();
        if (responseText == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final var obj = (T) xmlManager.unmarshal(new StringReader(responseText));
        return obj;
    }

    /**
     * Creates a flight on the server.
     *
     * @param flight The flight to be created.
     * @throws FlightAlreadyExistsException If the flight already exists.
     * @throws IllegalStateException If flight creation fails.
     */
    @Override
    public void createFlight(Flight flight) throws FlightAlreadyExistsException {
        final CreateFlightResponse response = sendCommand(domainMapper.mapToCreateFlightCommand(flight));

        if (!response.isSuccess()) {
            switch (response.getError()) {
                case FlightAlreadyExists:
                    throw new FlightAlreadyExistsException(response.getNumber());
                case InternalError:
                default:
                    throw new IllegalStateException("Could not create flight");
            }
        }
    }

    /**
     * Creates an airplane on the server.
     *
     * @param airplane The airplane to be created.
     * @throws AirplaneAlreadyExistsException If the airplane already exists.
     * @throws IllegalStateException If airplane creation fails.
     */
    @Override
    public void createAirplane(Airplane airplane) throws AirplaneAlreadyExistsException {
        final CreateAirplaneResponse response = sendCommand(domainMapper.mapToCreateAirplaneCommand(airplane));

        if (!response.isSuccess()) {
            switch (response.getError()) {
                case AirplaneAlreadyExists:
                    throw new AirplaneAlreadyExistsException(response.getAirplaneId());
                case InternalError:
                default:
                    throw new IllegalStateException("Could not create airplane");
            }
        }
    }

    /**
     * Creates a scheduled flight on the server.
     *
     * @param scheduledFlight The scheduled flight to be created.
     * @throws ScheduledFlightAlreadyExistsException If the scheduled flight already exists.
     * @throws AirplaneAlreadyScheduledException If the airplane is already assigned to another flight.
     * @throws ArrivalBeforeDepartureException If the arrival time is before the departure time.
     */
    @Override
    public void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException {
        final CreateScheduledFlightResponse response =
                sendCommand(domainMapper.mapToCreateScheduledFlightCommand(scheduledFlight));

        if (response.isSuccess()) {
            return;
        }
        if (Objects.requireNonNull(response.getError())
                == CreateScheduledFlightResponse.CsfrErrorType.AirplaneAlreadyScheduledException) {
            throw new AirplaneAlreadyScheduledException(
                    response.getAirplaneId(),
                    LocalTime.parse(response.getDepartureTime()),
                    LocalTime.parse(response.getArrivalTime()));
        }
        if (response.getError() == CreateScheduledFlightResponse.CsfrErrorType.ArrivalBeforeDepartureException) {
            throw new ArrivalBeforeDepartureException(
                    LocalTime.parse(response.getDepartureTime()), LocalTime.parse(response.getArrivalTime()));
        }
        if (response.getError() == CreateScheduledFlightResponse.CsfrErrorType.ScheduledFlightAlreadyExistsException) {
            throw new ScheduledFlightAlreadyExistsException(
                    response.getFlightId(), LocalDate.parse(response.getDepartureTime()));
        }
        throw new IllegalStateException("Could not create scheduled flight");
    }

    /**
     * Finds a flight by its number.
     *
     * @param number The flight number.
     * @return An optional containing the flight if found.
     * @throws IllegalStateException If the flight could not be retrieved.
     */
    @Override
    public Optional<Flight> findFlight(String number) {
        final FindFlightResponse response = sendCommand(domainMapper.mapToFindFlightCommand(number));
        if (!response.isFound()) {
            throw new IllegalStateException("Could not get flight");
        }
        return Optional.of(domainMapper.mapToFlight(response.getFlight()));
    }

    /**
     * Finds an airplane by its ID number.
     *
     * @param idNumber The airplane's ID.
     * @return An optional containing the airplane if found.
     * @throws IllegalStateException If the airplane could not be retrieved.
     */
    @Override
    public Optional<Airplane> findAirplane(String idNumber) {
        final FindAirplaneCommandResponse response = sendCommand(domainMapper.mapToFindAirplaneCommand(idNumber));

        if (!response.isFound()) {
            throw new IllegalStateException("Could not get airplane");
        }
        return Optional.of(domainMapper.mapToAirplane(response.getAirplaneItem()));
    }

    /**
     * Retrieves all flights.
     *
     * @return A list of all flights.
     * @throws IllegalStateException If the flights could not be retrieved.
     */
    @Override
    public List<Flight> getFlights() {
        final GetFlightsResponse response = sendCommand(new GetFlightsCommand());

        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not get flights");
        }

        return response.getFlights().stream().map(domainMapper::mapToFlight).toList();
    }

    /**
     * Retrieves all airplanes.
     *
     * @return A list of all airplanes.
     * @throws IllegalStateException If the airplanes could not be retrieved.
     */
    @Override
    public List<Airplane> getAirplanes() {
        final GetAirplanesResponse response = sendCommand(new GetAirplanesCommand());

        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not get airplanes");
        }

        return response.getAirplanes().stream().map(domainMapper::mapToAirplane).toList();
    }

    /**
     * Retrieves all scheduled flights.
     *
     * @return A list of all scheduled flights.
     * @throws IllegalStateException If the scheduled flights could not be retrieved.
     */
    @Override
    public List<ScheduledFlight> getScheduledFlights() {
        final GetScheduledFlightsResponse response = sendCommand(new GetScheduledFlightsCommand());

        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not get scheduled flights");
        }

        return response.getScheduledFlights().stream()
                .map(domainMapper::mapToScheduledFlight)
                .toList();
    }

    /**
     * Finds a scheduled flight by its number and date.
     *
     * @param flightNumber The flight number.
     * @param localDate The date of the flight.
     * @return An optional containing the scheduled flight if found.
     * @throws IllegalStateException If the scheduled flight could not be retrieved.
     */
    @Override
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
        final FindScheduledFlightResponse response =
                sendCommand(domainMapper.mapToFindScheduledFlightCommand(flightNumber, localDate));

        if (!response.isFound()) {
            throw new IllegalStateException("Could not get scheduled Flight");
        }
        return Optional.of(domainMapper.mapToScheduledFlight(response.getScheduledFlightItem()));
    }
}
