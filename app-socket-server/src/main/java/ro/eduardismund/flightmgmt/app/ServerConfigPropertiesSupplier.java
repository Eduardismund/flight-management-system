package ro.eduardismund.flightmgmt.app;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;

/**
 * Supplier that provides a new {@link ServerConfigProperties} instance.
 */
@RequiredArgsConstructor
public class ServerConfigPropertiesSupplier implements Supplier<ServerConfigProperties> {
    private final ApplicationContext applicationContext;

    /**
     * Builds a ServerConfigProperties from ApplicationContext environment properties.
     *
     * @return {@link ServerConfigProperties} instance
     */
    @Override
    public ServerConfigProperties get() {
        final var env = applicationContext.getEnvironment();
        return ServerConfigProperties.builder()
                .port(Integer.parseInt(env.getProperty("serverSocket.port", "6000")))
                .host(env.getProperty("serverSocket.host", "localhost"))
                .build();
    }
}
