package ro.eduardismund.flightmgmt.repo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

/**
 * The {@code InmemFlightManagementRepository} class implements the {@link
 * FlightManagementRepository} interface and provides an in-memory implementation of the flight
 * management repository. This class is responsible for managing flight-related data, including
 * flights, airplanes, scheduled flights, and bookings. The data is persisted and loaded using the
 * {@link InmemFlightManagementPersistenceManager}.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class InmemFlightManagementRepository implements FlightManagementRepository {

    private final InmemFlightManagementPersistenceManager persistenceManager;

    private List<Flight> flights = new ArrayList<>();
    private List<ScheduledFlight> scheduledFlights = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Airplane> airplanes = new ArrayList<>();

    @Override
    public void setBookings(List<Booking> bookings) {
        this.bookings = new ArrayList<>(bookings);
    }

    @Override
    public void setAirplanes(List<Airplane> airplanes) {
        this.airplanes = new ArrayList<>(airplanes);
    }

    @Override
    public void setFlights(List<Flight> flights) {
        this.flights = new ArrayList<>(flights);
    }

    @Override
    public void setScheduledFlights(List<ScheduledFlight> scheduledFlights) {
        this.scheduledFlights = new ArrayList<>(scheduledFlights);
    }

    /**
     * Initializes the in-memory flight management repository by loading the state of the system from
     * the persistence manager. The method retrieves airplanes, flights, scheduled flights, and
     * bookings from persistent storage and sets them in the current repository.
     */
    public void init() {
        this.persistenceManager.load(InmemFlightManagementPersistenceManager.Objects.builder()
                .airplanes(airplanes)
                .flights(flights)
                .scheduledFlights(scheduledFlights)
                .bookings(bookings)
                .build());
    }

    @Override
    public void addFlight(Flight flight) {
        flights.add(flight);
        flush();
    }

    @Override
    public Optional<Flight> findFlight(String flightNumber) {
        return flights.stream()
                .filter(flight -> flight.getNumber().equals(flightNumber))
                .findAny();
    }

    @Override
    public List<ScheduledFlight> findScheduledFlightsForAirplane(String idNumber, LocalDate date) {
        return scheduledFlights.stream()
                .filter(scheduledFlight -> scheduledFlight
                                .getAirplane()
                                .getIdNumber()
                                .equals(idNumber)
                        && scheduledFlight.getDepartureTime().toLocalDate().equals(date))
                .toList();
    }

    @Override
    public void addScheduledFlight(ScheduledFlight scheduledFlight) {
        scheduledFlights.add(scheduledFlight);
        flush();
    }

    @Override
    public Optional<ScheduledFlight> findScheduledFlight(ScheduledFlight scheduledFlight) {
        return scheduledFlights.stream()
                .filter(scheduledFlight1 -> scheduledFlight
                                .getFlight()
                                .getNumber()
                                .equals(scheduledFlight1.getFlight().getNumber())
                        && scheduledFlight
                                .getAirplane()
                                .getIdNumber()
                                .equals(scheduledFlight1.getAirplane().getIdNumber()))
                .findAny();
    }

    @Override
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
        return scheduledFlights.stream()
                .filter(scheduledFlight -> scheduledFlight
                                .getFlight()
                                .getNumber()
                                .equals(flightNumber)
                        && scheduledFlight.getDepartureTime().toLocalDate().equals(localDate))
                .findAny();
    }

    @Override
    public void addBooking(Booking booking) {
        bookings.add(booking);
        flush();
    }

    @Override
    public void addAirplane(Airplane airplane) {
        airplanes.add(airplane);
        flush();
    }

    @Override
    public Optional<Airplane> findAirplane(String airplaneNumber) {
        return airplanes.stream()
                .filter(airplane -> airplane.getIdNumber().equals(airplaneNumber))
                .findAny();
    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public boolean contains(Airplane airplane) {
        return airplanes.stream().anyMatch(storedAirplane -> storedAirplane == airplane);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public boolean contains(Flight flight) {
        return flights.stream().anyMatch(storedFlight -> storedFlight == flight);
    }

    @Override
    public List<Flight> getFlights() {
        return Collections.unmodifiableList(flights); // Return an unmodifiable list
    }

    @Override
    public List<Airplane> getAirplanes() {
        return Collections.unmodifiableList(airplanes); // Return an unmodifiable list
    }

    @Override
    public List<ScheduledFlight> getScheduledFlights() {
        return Collections.unmodifiableList(scheduledFlights); // Return an unmodifiable list
    }

    @Override
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings); // Return an unmodifiable list
    }

    private void flush() {
        persistenceManager.dump(InmemFlightManagementPersistenceManager.Objects.builder()
                .airplanes(airplanes)
                .flights(flights)
                .scheduledFlights(scheduledFlights)
                .bookings(bookings)
                .build());
    }
}
