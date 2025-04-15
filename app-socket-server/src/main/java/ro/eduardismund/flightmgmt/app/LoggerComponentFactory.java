package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ComponentFactory;
import com.github.eduardismund.appctx.ComponentResolver;
import com.github.eduardismund.appctx.Environment;
import ro.eduardismund.flightmgmt.server.Logger;

/**
 * Factory to create a {@link Logger} instance.
 */
public class LoggerComponentFactory implements ComponentFactory<Logger> {

    /**
     * Creates a {@link Logger} with system output and a UTF-8 scanner.
     *
     * @param properties The environment properties.
     * @param resolver The component resolver.
     * @return A new {@link Logger} instance.
     */
    @Override
    public Logger createComponent(Environment properties, ComponentResolver resolver) {
        return new Logger(System.out);
    }
}
