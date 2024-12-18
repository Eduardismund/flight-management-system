package ro.eduardismund.flightmgmt.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings({"checkstyle:MethodName", "checkstyle:AbbreviationAsWordInName", "unchecked"})
class JavaSerializationFlightManagementPersistenceManagerTest {

    @TempDir
    private Path tempDir;

    private Path filePath;

    private JavaSerializationFlightManagementPersistenceManager subject;

    @BeforeEach
    void setUp() {
        filePath = tempDir.resolve(getClass().getSimpleName() + ".dat");
        subject = new JavaSerializationFlightManagementPersistenceManager(filePath);
    }

    @Test
    void dump_SerializesObjectsToFile() throws IOException {
        final var objects = mock(InmemFlightManagementPersistenceManager.Objects.class);
        subject.dump(objects);
        assertTrue(Files.exists(filePath), "The dump file should exist.");
        assertTrue(Files.size(filePath) > 100, "The dump file should not be empty.");
        Files.delete(filePath);
    }

    @Test
    void dump_throws_IOException() throws IOException {
        final var objects = mock(InmemFlightManagementPersistenceManager.Objects.class);
        final var subjectSpy = spy(subject);

        doAnswer(invocation -> {
                    throw new IOException("Test exception");
                })
                .when(subjectSpy)
                .createOutputStream(any());

        assertThrows(IOException.class, () -> subjectSpy.dump(objects));
    }

    @Test
    void copyObjectsFromInputStream() throws IOException, ClassNotFoundException {
        final var subjectSpy = spy(subject);
        try (final var ois = mock(ObjectInputStream.class)) {
            final var objs = mock(InmemFlightManagementPersistenceManager.Objects.class);

            final var readObjs = mock(InmemFlightManagementPersistenceManager.Objects.class);
            doReturn(readObjs).when(ois).readObject();

            doNothing().when(subjectSpy).copyObjects(readObjs, objs);

            subjectSpy.copyObjectsFromInputStream(ois, objs);

            verify(subjectSpy).copyObjects(readObjs, objs);
        }
    }

    @Test
    void copyObjectsFromInputStream_IOException() throws IOException, ClassNotFoundException {
        final var subjectSpy = spy(subject);
        try (final var ois = mock(ObjectInputStream.class)) {
            final var objs = mock(InmemFlightManagementPersistenceManager.Objects.class);

            final var ioEx = new IOException();
            doThrow(ioEx).when(ois).readObject();

            final var resEx = assertThrows(IOException.class, () -> subjectSpy.copyObjectsFromInputStream(ois, objs));
            assertSame(ioEx, resEx);

            verify(subjectSpy, never()).copyObjects(any(), any());
        }
    }

    @Test
    void doWithInputStream() throws IOException {
        final var objectInSerInFile = 11111L;
        setupTempFile(objectInSerInFile);
        final var consumer = mockConsumerOis();

        final var readObject = new Object[1];
        doAnswer(inv -> {
                    readObject[0] = inv.getArgument(0, ObjectInputStream.class).readObject();
                    return null;
                })
                .when(consumer)
                .accept(any());

        subject.doWithInputStream(consumer);

        verify(consumer).accept(any(ObjectInputStream.class));
        assertEquals(objectInSerInFile, readObject[0]);
    }

    @Test
    void load_InvokesCopyObjectsFromInputStream() throws IOException, ClassNotFoundException {
        final var objects = mock(InmemFlightManagementPersistenceManager.Objects.class);

        final var sourceObjects = mock(InmemFlightManagementPersistenceManager.Objects.class);
        setupTempFile(sourceObjects);

        final var spySubject = spy(subject);

        try (final var objectInputStream = mock(ObjectInputStream.class)) {

            doReturn(sourceObjects).when(objectInputStream).readObject();

            doAnswer(invocation -> {
                        Consumer<ObjectInputStream> consumer = invocation.getArgument(0, Consumer.class);
                        consumer.accept(objectInputStream);
                        return null;
                    })
                    .when(spySubject)
                    .doWithInputStream(any());

            spySubject.load(objects);

            verify(spySubject).copyObjectsFromInputStream(eq(objectInputStream), eq(objects));
        }
    }

    @Test
    void load_throwsIOException() {
        final var objects = mock(InmemFlightManagementPersistenceManager.Objects.class);
        final var spySubject = spy(subject);

        doAnswer(invocation -> {
                    throw new IOException("Test exception");
                })
                .when(spySubject)
                .doWithInputStream(any());

        assertThrows(IOException.class, () -> spySubject.load(objects));
    }

    @Test
    void copyObjects() {
        final var sourceObjects = mock(InmemFlightManagementPersistenceManager.Objects.class);
        final var targetObjects = mock(InmemFlightManagementPersistenceManager.Objects.class);

        final var sourceAirplanes = mock(List.class);
        final var targetAirplanes = mock(List.class);

        final var sourceFlights = mock(List.class);
        final var targetFlights = mock(List.class);

        final var targetBookings = mock(List.class);
        final var sourceBookings = mock(List.class);

        final var sourceScheduledFlights = mock(List.class);
        final var targetScheduledFlights = mock(List.class);

        when(sourceObjects.airplanes()).thenReturn(sourceAirplanes);
        when(targetObjects.airplanes()).thenReturn(targetAirplanes);

        when(sourceObjects.flights()).thenReturn(sourceFlights);
        when(targetObjects.flights()).thenReturn(targetFlights);

        when(sourceObjects.bookings()).thenReturn(sourceBookings);
        when(targetObjects.bookings()).thenReturn(targetBookings);

        when(sourceObjects.scheduledFlights()).thenReturn(sourceScheduledFlights);
        when(targetObjects.scheduledFlights()).thenReturn(targetScheduledFlights);

        subject.copyObjects(sourceObjects, targetObjects);

        verify(targetAirplanes).clear();
        verify(targetAirplanes).addAll(sourceAirplanes);

        verify(targetFlights).clear();
        verify(targetFlights).addAll(sourceFlights);

        verify(targetScheduledFlights).clear();
        verify(targetScheduledFlights).addAll(sourceScheduledFlights);

        verify(targetBookings).clear();
        verify(targetBookings).addAll(sourceBookings);
    }

    @Test
    void doWithInputStream_IOException() throws IOException {
        final var objectInSerInFile = 11111L;
        setupTempFile(objectInSerInFile);

        final var consumer = mockConsumerOis();

        final var ioEx = new RuntimeException();

        doThrow(ioEx).when(consumer).accept(any(ObjectInputStream.class));

        assertThrows(RuntimeException.class, () -> subject.doWithInputStream(consumer));

        verify(consumer).accept(any(ObjectInputStream.class));
    }

    @SuppressWarnings("unchecked")
    private static Consumer<ObjectInputStream> mockConsumerOis() {
        return mock(Consumer.class);
    }

    private void setupTempFile(Serializable object) throws IOException {
        try (var oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(object);
        }
    }
}
