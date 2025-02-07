package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.net.ConnectException;
import java.net.Socket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;

class ClientSocketComponentFactoryTest {

    @Test
    void createComponent() {
        final var resolver = mock(ComponentResolver.class);
        final var properties = mock(Environment.class);
        final var config = mock(ServerConfigProperties.class);

        doReturn(config).when(resolver).resolveComponent(ServerConfigProperties.class);

        final var clientSocket = spy(ClientSocketComponentFactory.class);
        doReturn(new Socket()).when(clientSocket).createClientSocket(any());
        final var socket = clientSocket.createComponent(properties, resolver);

        assertNotNull(socket);
        assertInstanceOf(Socket.class, socket);
    }

    @SneakyThrows
    @Test
    void createClientSocket_throwsException() {

        int port = 100;

        final var config = mock(ServerConfigProperties.class);
        doReturn("localhost").when(config).getHost();
        doReturn(port).when(config).getPort();

        final var factory = new ClientSocketComponentFactory();
        assertThrows(ConnectException.class, () -> factory.createClientSocket(config));
    }
}
