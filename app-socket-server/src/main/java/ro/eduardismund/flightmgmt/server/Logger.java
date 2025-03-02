package ro.eduardismund.flightmgmt.server;

import java.io.PrintStream;
import lombok.RequiredArgsConstructor;

/**
 * Used to log different messages and exception for the server.
 */
@RequiredArgsConstructor
public class Logger {
    public final PrintStream out;

    /**
     * Print a String.
     *
     * @param message the string to print
     */
    public Logger println(String message) {
        out.println(message);
        return this;
    }

    /**
     * Prints exception details.
     */
    public void printException(Throwable throwable) {
        throwable.printStackTrace(out);
    }
}
