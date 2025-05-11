package ro.eduardismund.flightmgmt.app;

import java.util.function.Supplier;
import ro.eduardismund.flightmgmt.server.Logger;

/**
 * Supplier to provide a {@link Logger} instance.
 */
public class LoggerSupplier implements Supplier<Logger> {

    /**
     * Creates a {@link Logger} with system output and a UTF-8 scanner.
     *
     * @return A new {@link Logger} instance.
     */
    @Override
    public Logger get() {
        return new Logger(System.out);
    }
}
