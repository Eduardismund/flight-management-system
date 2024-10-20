package ro.eduardismund.flightmgmt.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

/**
 * The {@code Service} class provides business logic for managing flights, airplanes, and scheduled
 * flights within the flight management system. It acts as a layer between the user interface and
 * the data repository, ensuring that operations adhere to business rules and constraints.
 */
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Service {
    private final FlightManagementRepository repository;

    /**
     * Calls the repository in order to add a booking.
     *
     * @param booking to be added
     */
    public void createBooking(Booking booking) {
        repository.addBooking(booking);
    }

    /**
     * Creates a new flight in the repository if a flight with the same number doesn't already exist.
     *
     * @param flight the {@link Flight} object to be created
     * @throws FlightAlreadyExistsException if a flight with the same number already exists
     */
    public void createFlight(Flight flight) throws FlightAlreadyExistsException {
        if (repository.findFlight(flight.getNumber()).isPresent()) {
            throw new FlightAlreadyExistsException(flight.getNumber());
        }
        repository.addFlight(flight);
    }

    /**
     * Creates a new airplane in the repository if an airplane with the same ID does not already
     * exist.
     *
     * @param airplane the {@link Airplane} object to be created
     * @throws AirplaneAlreadyExistsException if an airplane with the same ID already exists
     */
    public void createAirplane(Airplane airplane) throws AirplaneAlreadyExistsException {
        if (repository.findAirplane(airplane.getIdNumber()).isPresent()) {
            throw new AirplaneAlreadyExistsException(airplane.getIdNumber());
        }
        repository.addAirplane(airplane);
    }

    /**
     * Checks if two scheduled flights overlap in their departure and arrival times.
     *
     * @param scheduledFlight1 the first scheduled flight
     * @param scheduledFlight2 the second scheduled flight
     * @return {@code true} if the flights overlap, {@code false} otherwise
     */
    static boolean isOverlapping(ScheduledFlight scheduledFlight1, ScheduledFlight scheduledFlight2) {
        final var maxDepartureTime = Stream.of(scheduledFlight1.getDepartureTime(), scheduledFlight2.getDepartureTime())
                .max(Comparator.naturalOrder())
                .get();
        final var minDepartureTime = Stream.of(scheduledFlight1.getArrivalTime(), scheduledFlight2.getArrivalTime())
                .min(Comparator.naturalOrder())
                .get();
        return maxDepartureTime.isBefore(minDepartureTime);
    }

    /**
     * Creates a new scheduled flight in the repository if it does not conflict with existing
     * scheduled flights or violate business rules regarding arrival and departure times.
     *
     * @param scheduledFlight the {@link ScheduledFlight} object to be created
     * @throws ScheduledFlightAlreadyExistsException if a scheduled flight with the same number
     *     already exists on the same date
     * @throws AirplaneAlreadyScheduledException if the airplane is already scheduled for a
     *     conflicting flight
     * @throws ArrivalBeforeDepartureException if the arrival time is before the departure time
     */
    public void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException {
        final var flight = Optional.ofNullable(scheduledFlight.getFlight())
                .filter(repository::contains)
                .orElseThrow(() -> new IllegalArgumentException("Missing or invalid flight!"));

        final var airplane = Optional.ofNullable(scheduledFlight.getAirplane())
                .filter(repository::contains)
                .orElseThrow(() -> new IllegalArgumentException("Missing or invalid airplane!"));

        if (repository
                .findScheduledFlight(
                        flight.getNumber(), scheduledFlight.getDepartureTime().toLocalDate())
                .isPresent()) {
            throw new ScheduledFlightAlreadyExistsException(
                    flight.getNumber(), scheduledFlight.getDepartureTime().toLocalDate());
        }

        if (repository
                .findScheduledFlightsForAirplane(
                        airplane.getIdNumber(),
                        scheduledFlight.getDepartureTime().toLocalDate())
                .stream()
                .anyMatch(scheduledFlight1 -> isOverlapping(scheduledFlight1, scheduledFlight))) {
            throw new AirplaneAlreadyScheduledException(
                    airplane.getIdNumber(),
                    scheduledFlight.getDepartureTime().toLocalTime(),
                    scheduledFlight.getArrivalTime().toLocalTime());
        }

        if (!scheduledFlight.getDepartureTime().isBefore(scheduledFlight.getArrivalTime())) {
            throw new ArrivalBeforeDepartureException(
                    scheduledFlight.getDepartureTime().toLocalTime(),
                    scheduledFlight.getArrivalTime().toLocalTime());
        }
        repository.addScheduledFlight(scheduledFlight);
    }

    /**
     * Finds a flight in the repository by its flight number.
     *
     * @param number the flight number to search for
     * @return an {@link Optional} containing the found {@link Flight} if it exists, or an empty
     *     Optional if not
     */
    public Optional<Flight> findFlight(String number) {
        return repository.findFlight(number);
    }

    /**
     * Finds an airplane in the repository by its ID number.
     *
     * @param idNumber the ID number of the airplane to search for
     * @return an {@link Optional} containing the found {@link Airplane} if it exists, or an empty
     *     Optional if not
     */
    public Optional<Airplane> findAirplane(String idNumber) {
        return repository.findAirplane(idNumber);
    }

    /**
     * Retrieves all flights from the repository.
     *
     * @return a list of {@link Flight} objects
     */
    public List<Flight> getFlights() {
        return repository.getFlights();
    }

    /**
     * Retrieves all airplanes from the repository.
     *
     * @return a list of {@link Airplane} objects
     */
    public List<Airplane> getAirplanes() {
        return repository.getAirplanes();
    }

    /**
     * Retrieves all scheduled flights from the repository.
     *
     * @return a list of {@link ScheduledFlight} objects
     */
    public List<ScheduledFlight> getScheduledFlights() {
        return repository.getScheduledFlights();
    }

    /**
     * Finds a scheduled flight in the repository by flight number and date.
     *
     * @param flightNumber the flight number to search for
     * @param localDate the date of the scheduled flight
     * @return an {@link Optional} containing the found {@link ScheduledFlight} if it exists, or an
     *     empty Optional if not
     */
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
        return repository.findScheduledFlight(flightNumber, localDate);
    }
}
