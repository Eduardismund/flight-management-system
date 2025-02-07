package ro.eduardismund.flightmgmt.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Flight;

@ExtendWith(MockitoExtension.class)
class CliManagerTest {
    public static final String INVALID = "invalid";

    @Mock
    private PrintStream out;

    @Mock
    private Scanner scanner;

    private CliManager manager;

    @BeforeEach
    void setUp() {
        manager = new CliManager(out, scanner);
    }

    @Test
    void println() {
        String message = "Test message";

        manager.println(message);

        verify(out).println(message);
    }

    @Test
    void readDateTime() {

        when(scanner.nextLine()).thenReturn(INVALID, "2024-12-12T12:12:12");

        final var actualDate = manager.readDateTime("arrival");

        verify(out, times(2)).println("Insert arrival date: YYYY-MM-DDTHH:mm:ss");
        assertEquals(LocalDateTime.parse("2024-12-12T12:12:12"), actualDate);
    }

    @Test
    void readDate() {
        final var title = "Please insert the date of the flight: ";
        when(scanner.nextLine()).thenReturn(INVALID, "2024-12-12");

        final var actualDate = manager.readDate(title);

        assertEquals(LocalDate.parse("2024-12-12"), actualDate);
        verify(out, times(2)).println(title);
    }

    @Test
    void readById() {

        final var label = "flight";
        final var flightId = "F123";

        @SuppressWarnings("unchecked")
        Function<String, Optional<Flight>> mockFunction = mock(Function.class);

        when(scanner.nextLine()).thenReturn(INVALID, flightId);

        when(mockFunction.apply(INVALID)).thenReturn(Optional.empty());
        when(mockFunction.apply(flightId)).thenReturn(Optional.of(new Flight(flightId)));

        final var result = manager.readById(label, mockFunction);

        assertEquals(result.getNumber(), flightId);
        verify(out, times(2)).println("Insert " + label + " number: ");
        verify(out, times(1)).println(label + " with ID invalid not found. Please try again.");
    }

    @SuppressWarnings("UnnecessaryToStringCall")
    @Test
    void printAll() {
        final var testFlight = new Flight("F123");
        final var testIterable = List.of(testFlight);
        final var type = manager.printAll(testIterable);
        verify(out).println(testFlight.toString());
        assertNotNull(type);
    }

    @Test
    void readInt() {
        int expectedValue = 12;
        when(scanner.nextInt()).thenReturn(expectedValue);

        final var actualValue = manager.readInt();
        verify(scanner).nextInt();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void readLine() {
        String test = "test";
        when(scanner.nextLine()).thenReturn(test);

        final var result = manager.readLine();
        verify(scanner).nextLine();
        assertEquals(test, result);
    }

    @Test
    void printAll_existEntities() {
        final var title = "Entities";
        final var testList = List.of(new Flight("F123"), new Flight("456"));
        final var isEmptyMessage = "No entities found";
        final var result = manager.printAll(testList, title, isEmptyMessage);

        verify(out).println(testList.getFirst().toString());
        verify(out).println(testList.getLast().toString());
        verify(out).println(title);

        assertNotNull(result);
    }

    @Test
    void printAll_doesntExistEntities() {
        final var title = "Entities";
        final var isEmptyMessage = "No entities found";
        manager.printAll(List.of(), title, isEmptyMessage);

        verify(out).println(isEmptyMessage);
    }

    @Test
    void printException() {
        final var mockThrow = mock(RuntimeException.class);
        manager.printException(mockThrow);
        verify(mockThrow).printStackTrace(out);
    }
}
