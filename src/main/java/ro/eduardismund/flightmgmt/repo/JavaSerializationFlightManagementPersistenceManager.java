package ro.eduardismund.flightmgmt.repo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;

/**
 * The {@code JavaSerializationFlightManagementPersistenceManager} class implements the {@link
 * InmemFlightManagementPersistenceManager} interface and provides functionality for persisting and
 * loading flight management data using Java object serialization.
 *
 * <p>The data is stored in and retrieved from a file located at the specified path.
 *
 * <p>This class uses the standard Java serialization mechanism to serialize and deserialize the
 * flight management objects.
 */
@RequiredArgsConstructor
public class JavaSerializationFlightManagementPersistenceManager implements InmemFlightManagementPersistenceManager {

    private final Path filePath;

    /**
     * Creates a new output stream to write serialized data to the specified file.
     *
     * @param path the path to the file where the serialized data will be stored
     * @return a new output stream
     * @throws IOException if an I/O error occurs while creating the output stream
     */
    protected OutputStream createOutputStream(Path path) throws IOException {
        return Files.newOutputStream(path);
    }

    @SneakyThrows
    @Override
    public void dump(Objects objects) {
        try (final var out = createOutputStream(filePath)) {
            try (final var objectOutputStream = new ObjectOutputStream(out)) {
                objectOutputStream.writeObject(objects);
            }
        }
    }

    @SneakyThrows
    @Override
    public void load(Objects objects) {
        doWithInputStream(objectInputStream -> copyObjectsFromInputStream(objectInputStream, objects));
    }

    @SneakyThrows
    void doWithInputStream(Consumer<ObjectInputStream> consumer) {
        if (!filePath.toFile().exists()) {
            return;
        }
        try (final var in = Files.newInputStream(filePath)) {
            try (final var objectOutputStream = new ObjectInputStream(in)) {
                consumer.accept(objectOutputStream);
            }
        }
    }

    @SneakyThrows
    void copyObjectsFromInputStream(ObjectInputStream objectInputStream, Objects objects) {
        final var readObjects = (Objects) objectInputStream.readObject();
        copyObjects(readObjects, objects);
    }

    void copyObjects(Objects sourceObjects, Objects targetObjects) {

        List<Airplane> targetAirplanes = targetObjects.airplanes();
        List<Airplane> sourceAirplanes = sourceObjects.airplanes();
        targetAirplanes.clear();
        targetAirplanes.addAll(sourceAirplanes);

        List<Flight> targetFlights = targetObjects.flights();
        List<Flight> sourceFlights = sourceObjects.flights();
        targetFlights.clear();
        targetFlights.addAll(sourceFlights);

        List<ScheduledFlight> targetScheduledFlights = targetObjects.scheduledFlights();
        List<ScheduledFlight> sourceScheduledFlights = sourceObjects.scheduledFlights();
        targetScheduledFlights.clear();
        targetScheduledFlights.addAll(sourceScheduledFlights);

        List<Booking> targetBookings = targetObjects.bookings();
        List<Booking> sourceBookings = sourceObjects.bookings();
        targetBookings.clear();
        targetBookings.addAll(sourceBookings);
    }
}
