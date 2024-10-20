package ro.eduardismund.flightmgmt.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

/**
 * The {@code FlightManagementRepository} interface defines the operations for managing flights,
 * scheduled flights, airplanes, and bookings within the flight management system. It provides
 * methods for adding, finding, and managing entities related to flight operations.
 */
public interface FlightManagementRepository {

    /**
     * Adds a new flight to the repository.
     *
     * @param flight the flight to be added
     */
    void addFlight(Flight flight);

    /**
     * Finds a flight by its flight number.
     *
     * @param flightNumber the unique flight number
     * @return an {@code Optional} containing the flight if found, or empty if not found
     */
    Optional<Flight> findFlight(String flightNumber);

    /**
     * Finds all scheduled flights for a specific airplane on a given date.
     *
     * @param idNumber the ID number of the airplane
     * @param date the date for which to find the scheduled flights
     * @return a list of scheduled flights for the given airplane and date
     */
    List<ScheduledFlight> findScheduledFlightsForAirplane(String idNumber, LocalDate date);

    /**
     * Adds a new scheduled flight to the repository.
     *
     * @param scheduledFlight the scheduled flight to be added
     */
    void addScheduledFlight(ScheduledFlight scheduledFlight);

    /**
     * Finds a specific scheduled flight by its details.
     *
     * @param scheduledFlight the scheduled flight to find
     * @return an {@code Optional} containing the scheduled flight if found, or empty if not found
     */
    Optional<ScheduledFlight> findScheduledFlight(ScheduledFlight scheduledFlight);

    /**
     * Finds a scheduled flight by its flight number and date.
     *
     * @param flightNumber the flight number
     * @param localDate the date of the scheduled flight
     * @return an {@code Optional} containing the scheduled flight if found, or empty if not found
     */
    Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate);

    /**
     * Adds a new booking to the repository.
     *
     * @param booking the booking to be added
     */
    void addBooking(Booking booking);

    /**
     * Adds a new airplane to the repository.
     *
     * @param airplane the airplane to be added
     */
    void addAirplane(Airplane airplane);

    /**
     * Finds an airplane by its ID number.
     *
     * @param airplaneNumber the ID number of the airplane
     * @return an {@code Optional} containing the airplane if found, or empty if not found
     */
    Optional<Airplane> findAirplane(String airplaneNumber);

    /**
     * Checks if the repository contains a specific airplane.
     *
     * @param airplane the airplane to check
     * @return {@code true} if the repository contains the airplane, {@code false} otherwise
     */
    boolean contains(Airplane airplane);

    /**
     * Checks if the repository contains a specific flight.
     *
     * @param flight the flight to check
     * @return {@code true} if the repository contains the flight, {@code false} otherwise
     */
    boolean contains(Flight flight);

    /**
     * Retrieves all flights stored in the repository.
     *
     * @return a list of all flights
     */
    List<Flight> getFlights();

    /**
     * Retrieves all airplanes stored in the repository.
     *
     * @return a list of all airplanes
     */
    List<Airplane> getAirplanes();

    /**
     * Retrieves all scheduled flights stored in the repository.
     *
     * @return a list of all scheduled flights
     */
    List<ScheduledFlight> getScheduledFlights();

    /**
     * Retrieves all bookings stored in the repository.
     *
     * @return a list of all bookings
     */
    List<Booking> getBookings();

    /**
     * Replaces the current list of airplanes in the repository with a new list.
     *
     * @param airplanes the new list of airplanes
     */
    void setAirplanes(List<Airplane> airplanes);

    /**
     * Replaces the current list of flights in the repository with a new list.
     *
     * @param flights the new list of flights
     */
    void setFlights(List<Flight> flights);

    /**
     * Replaces the current list of scheduled flights in the repository with a new list.
     *
     * @param scheduledFlights the new list of scheduled flights
     */
    void setScheduledFlights(List<ScheduledFlight> scheduledFlights);

    /**
     * Replaces the current list of bookings in the repository with a new list.
     *
     * @param bookings the new list of bookings
     */
    void setBookings(List<Booking> bookings);
}
