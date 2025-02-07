package ro.eduardismund.flightmgmt.cli;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Command Line Interface Manager allows writing and reading to and from the Standard Input/Output.
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
@AllArgsConstructor
public class CliManager {

    public PrintStream out;
    public Scanner scanner;

    /**
     * Print a String.
     *
     * @param message the string to print
     */
    public CliManager println(String message) {
        out.println(message);
        return this;
    }

    /**
     * Reads the data input from the keyboard for the date in the correct format.
     *
     * @param type either arrival or departure
     * @return the date of the departure/arrival
     */
    public LocalDateTime readDateTime(String type) {
        Optional<LocalDateTime> dateTime;
        do {
            println("Insert " + type + " date: YYYY-MM-DDTHH:mm:ss");
            try {
                dateTime = Optional.of(LocalDateTime.parse(readLine()));
            } catch (DateTimeParseException ignored) {
                dateTime = Optional.empty();
            }
        } while (dateTime.isEmpty());
        return dateTime.get();
    }

    /**
     * Reads the date of the flight, making sure the format is respected.
     *
     * @return the date of the flight
     */
    public LocalDate readDate(String sentence) {
        Optional<LocalDate> date;
        do {
            println(sentence);
            try {
                date = Optional.of(LocalDate.parse(readLine()));
            } catch (DateTimeParseException e) {
                date = Optional.empty();
            }
        } while (date.isEmpty());
        return date.get();
    }

    /**
     * function that uses a template in order to find a flight or an airplane in the repository.
     *
     * @param idName     of the required entity
     * @param findMethod of the required entity
     * @param <T>        Flight/Airplane
     * @return either a Flight or an Airplane
     */
    public <T> T readById(String idName, Function<String, Optional<T>> findMethod) {
        Optional<T> optional;

        do {
            println("Insert " + idName + " number: ");
            final String num = readLine();

            optional = findMethod.apply(num);

            if (optional.isEmpty()) {
                println(idName + " with ID " + num + " not found. Please try again.");
            }

        } while (optional.isEmpty());

        return optional.get();
    }

    /**
     * Print a list.
     *
     * @param entities the list to print
     */
    CliManager printAll(Iterable<?> entities) {
        for (final Object entity : entities) {
            println(entity.toString());
        }
        return this;
    }

    /**
     * Prints all entities in a collection with a title and an empty message if needed.
     */
    CliManager printAll(Collection<?> entities, String title, String isEmptyMessage) {
        if (entities.isEmpty()) {
            println(isEmptyMessage);
        } else {
            println(title).printAll(entities);
        }

        return this;
    }

    /**
     * Reads an integer from the user.
     */
    public int readInt() {
        return scanner.nextInt();
    }

    /**
     * Reads a line from the user.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Prints exception details.
     */
    public void printException(Throwable throwable) {
        throwable.printStackTrace(out);
    }
}
