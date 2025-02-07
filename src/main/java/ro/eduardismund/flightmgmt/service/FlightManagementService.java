package ro.eduardismund.flightmgmt.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

/**
 * Interface defining the operations for managing flights, airplanes, bookings, and scheduled flights.
 */
public interface FlightManagementService {

    /**
     * Creates a booking.
     *
     * @param booking The booking to be created.
     */
    void createBooking(Booking booking);

    /**
     * Creates a flight.
     *
     * @param flight The flight to be created.
     * @throws FlightAlreadyExistsException If a flight with the same details already exists.
     */
    void createFlight(Flight flight) throws FlightAlreadyExistsException;

    /**
     * Creates an airplane.
     *
     * @param airplane The airplane to be created.
     * @throws AirplaneAlreadyExistsException If an airplane with the same ID already exists.
     */
    void createAirplane(Airplane airplane) throws AirplaneAlreadyExistsException;

    /**
     * Creates a scheduled flight.
     *
     * @param scheduledFlight The scheduled flight to be created.
     * @throws ScheduledFlightAlreadyExistsException If the scheduled flight already exists.
     * @throws AirplaneAlreadyScheduledException If the airplane is already scheduled for another flight.
     * @throws ArrivalBeforeDepartureException If the arrival time is before the departure time.
     */
    void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException;

    /**
     * Finds a flight by its flight number.
     *
     * @param number The flight number to search for.
     * @return An {@link Optional} containing the found flight, or empty if no flight is found.
     */
    Optional<Flight> findFlight(String number);

    /**
     * Finds an airplane by its ID number.
     *
     * @param idNumber The ID number of the airplane to search for.
     * @return An {@link Optional} containing the found airplane, or empty if no airplane is found.
     */
    Optional<Airplane> findAirplane(String idNumber);

    /**
     * Retrieves a list of all flights.
     *
     * @return A list of all flights.
     */
    List<Flight> getFlights();

    /**
     * Retrieves a list of all airplanes.
     *
     * @return A list of all airplanes.
     */
    List<Airplane> getAirplanes();

    /**
     * Retrieves a list of all scheduled flights.
     *
     * @return A list of all scheduled flights.
     */
    List<ScheduledFlight> getScheduledFlights();

    /**
     * Finds a scheduled flight by flight number and date.
     *
     * @param flightNumber The flight number of the scheduled flight.
     * @param localDate The date of the scheduled flight.
     * @return An {@link Optional} containing the found scheduled flight, or empty if no scheduled flight is found.
     */
    Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate);
}
