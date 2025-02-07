package ro.eduardismund.flightmgmt.app;

import java.net.Socket;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;

/**
 * Factory class for creating a client {@link Socket} using server configuration.
 */
public class ClientSocketComponentFactory implements ComponentFactory<Socket> {

    /**
     * Creates a {@link Socket} using the given environment properties and resolver.
     *
     * @param properties The environment properties (not used in this implementation).
     * @param resolver The component resolver to resolve dependencies.
     * @return A new {@link Socket} connected to the server.
     */
    @Override
    public Socket createComponent(Environment properties, ComponentResolver resolver) {
        final var config = resolver.resolveComponent(ServerConfigProperties.class);
        return createClientSocket(config);
    }

    /**
     * Creates a {@link Socket} using the provided server configuration.
     *
     * @param config The server configuration containing the host and port.
     * @return A new {@link Socket} connected to the specified host and port.
     */
    @SneakyThrows
    Socket createClientSocket(ServerConfigProperties config) {
        return new Socket(config.getHost(), config.getPort());
    }
}
