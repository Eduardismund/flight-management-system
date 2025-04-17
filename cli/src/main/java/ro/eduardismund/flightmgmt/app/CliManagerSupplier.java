package ro.eduardismund.flightmgmt.app;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Supplier;
import ro.eduardismund.flightmgmt.cli.CliManager;

/**
 * Supplier to provide a {@link CliManager} instance.
 */
public class CliManagerSupplier implements Supplier<CliManager> {

    /**
     * Creates a {@link CliManager} with system output and a UTF-8 scanner.
     *
     * @return A new {@link CliManager} instance.
     */
    @Override
    public CliManager get() {
        return new CliManager(System.out, new Scanner(System.in, StandardCharsets.UTF_8));
    }
}
