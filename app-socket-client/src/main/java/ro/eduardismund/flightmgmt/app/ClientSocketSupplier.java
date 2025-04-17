package ro.eduardismund.flightmgmt.app;

import java.net.Socket;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;

/**
 * Factory class for creating a client {@link Socket} using server configuration.
 */
@RequiredArgsConstructor
public class ClientSocketSupplier implements Supplier<Socket> {

    private final ApplicationContext applicationContext;

    /**
     * Creates a {@link Socket} using the configuration found in the wrapped application context.
     *
     * @return A new {@link Socket} connected to the server.
     */
    @Override
    public Socket get() {
        final var port = Integer.parseInt(applicationContext.getEnvironment().getProperty("serverSocket.port", "6000"));
        final var host = applicationContext.getEnvironment().getProperty("serverSocket.host", "localhost");
        return createClientSocket(port, host);
    }

    /**
     * Creates a {@link Socket} using the provided server configuration.
     *
     * @return A new {@link Socket} connected to the specified host and port.
     */
    @SneakyThrows
    Socket createClientSocket(int port, String host) {
        return new Socket(host, port);
    }
}
