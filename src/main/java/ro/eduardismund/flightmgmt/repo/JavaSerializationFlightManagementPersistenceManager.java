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
        try (var out = createOutputStream(filePath)) {
            try (var objectOutputStream = new ObjectOutputStream(out)) {
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
        try (var inputStream = Files.newInputStream(filePath)) {
            try (var objectOutputStream = new ObjectInputStream(inputStream)) {
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

        final List<Airplane> targetAirplanes = targetObjects.airplanes();
        final List<Airplane> sourceAirplanes = sourceObjects.airplanes();
        targetAirplanes.clear();
        targetAirplanes.addAll(sourceAirplanes);

        final List<Flight> targetFlights = targetObjects.flights();
        final List<Flight> sourceFlights = sourceObjects.flights();
        targetFlights.clear();
        targetFlights.addAll(sourceFlights);

        final List<ScheduledFlight> targetScheduled = targetObjects.scheduledFlights();
        final List<ScheduledFlight> sourceScheduled = sourceObjects.scheduledFlights();
        targetScheduled.clear();
        targetScheduled.addAll(sourceScheduled);

        final List<Booking> targetBookings = targetObjects.bookings();
        final List<Booking> sourceBookings = sourceObjects.bookings();
        targetBookings.clear();
        targetBookings.addAll(sourceBookings);
    }
}
