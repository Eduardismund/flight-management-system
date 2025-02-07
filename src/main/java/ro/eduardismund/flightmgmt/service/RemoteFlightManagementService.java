package ro.eduardismund.flightmgmt.service;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.server.*;

public class RemoteFlightManagementService implements FlightManagementService {

    private final DomainMapper domainMapper;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Marshaller mar = JaxbUtil.createMarshaller();
    private final Unmarshaller unmar = JaxbUtil.createUnmarshaller();

    @SneakyThrows
    public RemoteFlightManagementService(DomainMapper domainMapper, Socket socket) throws JAXBException {
        this.domainMapper = domainMapper;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void createBooking(Booking booking) {}

    @SneakyThrows
    private <T> T sendCommand(Object command) {
        mar.marshal(command, out);
        out.println();
        out.flush();
        final var responseText = in.readLine();
        @SuppressWarnings("unchecked")
        final var obj = (T) unmar.unmarshal(new StringReader(responseText));
        return obj;
    }

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

    @Override
    public void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException {}

    @Override
    public Optional<Flight> findFlight(String number) {
        return Optional.empty();
    }

    @Override
    public Optional<Airplane> findAirplane(String idNumber) {
        return Optional.empty();
    }

    @Override
    public List<Flight> getFlights() {
        final GetFlightsResponse response = sendCommand(new GetFlightsCommand());

        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not get flights");
        }

        return response.getFlights().stream().map(this::mapFlightItem).toList();
    }

    private Flight mapFlightItem(FlightItem flightItem) {
        final var flight = new Flight(flightItem.getNumber());
        flight.setCompany(flightItem.getCompany());
        return flight;
    }

    @Override
    public List<Airplane> getAirplanes() {
        final GetAirplanesResponse response = sendCommand(new GetAirplanesCommand());

        if (!response.isSuccess()) {
            throw new IllegalStateException("Could not get airplanes");
        }

        return response.getAirplanes().stream().map(domainMapper::mapFromAirplaneItem).toList();

    }

    @Override
    public List<ScheduledFlight> getScheduledFlights() {
        return List.of();
    }

    @Override
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
        return Optional.empty();
    }
}
