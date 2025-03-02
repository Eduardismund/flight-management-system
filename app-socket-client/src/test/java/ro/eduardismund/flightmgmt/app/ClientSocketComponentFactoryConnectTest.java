package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.ServerSocket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class ClientSocketComponentFactoryConnectTest {

    @SneakyThrows
    @Test
    void createClientSocket() {
        final var serverSocket = new ServerSocket(0); // 0 = OS assigns a free port

        int port = serverSocket.getLocalPort();

        final var config = mock(ServerConfigProperties.class);
        doReturn("localhost").when(config).getHost();
        doReturn(port).when(config).getPort();

        final var factory = new ClientSocketComponentFactory();
        final var clientSocket = factory.createClientSocket(config);

        assertNotNull(clientSocket);
        assertEquals("localhost", clientSocket.getInetAddress().getHostName());
        assertEquals(port, clientSocket.getPort());

        assertFalse(clientSocket.isClosed());
        serverSocket.close();
    }
}
