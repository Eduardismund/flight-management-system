package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

class FlightManagementRepositoryTest {

    @Test
    void init() {
        final var repo = new FlightManagementRepositoryImpl();
        assertDoesNotThrow(repo::init);
    }

    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    private static class FlightManagementRepositoryImpl implements FlightManagementRepository {
        @Override
        public void addFlight(Flight flight) {}

        @Override
        public Optional<Flight> findFlight(String flightNumber) {
            return Optional.empty();
        }

        @Override
        public List<ScheduledFlight> findScheduledFlightsForAirplane(String idNumber, LocalDate date) {
            return List.of();
        }

        @Override
        public void addScheduledFlight(ScheduledFlight scheduledFlight) {}

        @Override
        public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {
            return Optional.empty();
        }

        @Override
        public void addBooking(Booking booking) {}

        @Override
        public void addAirplane(Airplane airplane) {}

        @Override
        public Optional<Airplane> findAirplane(String airplaneNumber) {
            return Optional.empty();
        }

        @Override
        public boolean contains(Airplane airplane) {
            return false;
        }

        @Override
        public boolean contains(Flight flight) {
            return false;
        }

        @Override
        public List<Flight> getFlights() {
            return List.of();
        }

        @Override
        public List<Airplane> getAirplanes() {
            return List.of();
        }

        @Override
        public List<ScheduledFlight> getScheduledFlights() {
            return List.of();
        }
    }
}
