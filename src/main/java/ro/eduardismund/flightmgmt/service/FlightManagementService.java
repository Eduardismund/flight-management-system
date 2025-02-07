package ro.eduardismund.flightmgmt.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

public interface FlightManagementService {
    void createBooking(Booking booking);

    void createFlight(Flight flight) throws FlightAlreadyExistsException;

    void createAirplane(Airplane airplane) throws AirplaneAlreadyExistsException;

    void createScheduledFlight(ScheduledFlight scheduledFlight)
            throws ScheduledFlightAlreadyExistsException, AirplaneAlreadyScheduledException,
                    ArrivalBeforeDepartureException;

    Optional<Flight> findFlight(String number);

    Optional<Airplane> findAirplane(String idNumber);

    List<Flight> getFlights();

    List<Airplane> getAirplanes();

    List<ScheduledFlight> getScheduledFlights();

    Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate);
}
