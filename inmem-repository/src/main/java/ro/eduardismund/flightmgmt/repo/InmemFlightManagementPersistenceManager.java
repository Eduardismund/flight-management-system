package ro.eduardismund.flightmgmt.repo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

/**
 * The {@code InmemFlightManagementPersistenceManager} interface defines the methods for managing
 * the persistence of flight management entities in memory. It provides functionality to dump and
 * load the state of objects, such as airplanes, flights, scheduled flights, and bookings, to and
 * from persistent storage.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public interface InmemFlightManagementPersistenceManager {

    /**
     * A record class to hold a collection of airplanes, flights, scheduled flights, and bookings. It
     * is used for dumping and loading these entities in the persistence manager.
     */
    @Builder
    record Objects(
            List<Airplane> airplanes,
            List<Flight> flights,
            List<ScheduledFlight> scheduledFlights,
            List<Booking> bookings)
            implements Serializable {}

    /**
     * Dumps the provided objects (airplanes, flights, scheduled flights, and bookings) into
     * persistent storage.
     *
     * @param objects the collection of entities to be dumped to storage
     */
    void dump(Objects objects);

    /**
     * Loads the provided objects (airplanes, flights, scheduled flights, and bookings) from
     * persistent storage.
     *
     * @param objects the collection of entities to be loaded from storage
     */
    void load(Objects objects);
}
