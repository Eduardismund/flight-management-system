package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.ServerSocket;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

class ClientSocketSupplierConnectTest {

    @SneakyThrows
    @Test
    void createClientSocket() {
        final var serverSocket = new ServerSocket(0); // 0 = OS assigns a free port

        int port = serverSocket.getLocalPort();
        var host = "localhost";

        final var config = mock(Environment.class);
        doReturn(host).when(config).getProperty("serverSocket.host", "localhost");
        doReturn(String.valueOf(port)).when(config).getProperty("serverSocket.port", "6000");

        final var mockAppContext = mock(ApplicationContext.class);
        doReturn(config).when(mockAppContext).getEnvironment();

        final var factory = new ClientSocketSupplier(mockAppContext);
        final var clientSocket = factory.createClientSocket(port, host);

        assertNotNull(clientSocket);
        assertEquals("localhost", clientSocket.getInetAddress().getHostName());
        assertEquals(port, clientSocket.getPort());

        assertFalse(clientSocket.isClosed());
        serverSocket.close();
    }
}
