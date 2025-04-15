package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ComponentFactory;
import com.github.eduardismund.appctx.ComponentResolver;
import com.github.eduardismund.appctx.Environment;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import ro.eduardismund.flightmgmt.cli.CliManager;

/**
 * Factory to create a {@link CliManager} instance.
 */
public class CliManagerComponentFactory implements ComponentFactory<CliManager> {

    /**
     * Creates a {@link CliManager} with system output and a UTF-8 scanner.
     *
     * @param properties The environment properties.
     * @param componentResolver The component resolver.
     * @return A new {@link CliManager} instance.
     */
    @Override
    public CliManager createComponent(Environment properties, ComponentResolver componentResolver) {
        return new CliManager(System.out, new Scanner(System.in, StandardCharsets.UTF_8));
    }
}
