package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

class ClientSocketSupplierTest {

    @SuppressWarnings("resource")
    @Test
    void get() throws IOException {
        final var config = mock(Environment.class);
        final var serverSocket = new ServerSocket(0); // 0 = OS assigns a free port

        var port = String.valueOf(serverSocket.getLocalPort());
        var host = "localhost";
        doReturn(host).when(config).getProperty("serverSocket.host", "localhost");
        doReturn(port).when(config).getProperty("serverSocket.port", "6000");

        final var mockAppContext = mock(ApplicationContext.class);
        doReturn(config).when(mockAppContext).getEnvironment();

        final var spyClientSocket = Mockito.spy(new ClientSocketSupplier(mockAppContext));
        doReturn(new Socket()).when(spyClientSocket).createClientSocket(anyInt(), anyString());
        final var socket = spyClientSocket.get();

        assertNotNull(socket);
        assertInstanceOf(Socket.class, socket);
    }

    @SuppressWarnings("resource")
    @Test
    void createClientSocket_throwsException() {

        int port = 100;
        var host = "localhost";

        final var mockAppContext = mock(ApplicationContext.class);
        final var supplier = new ClientSocketSupplier(mockAppContext);
        assertThrows(ConnectException.class, () -> supplier.createClientSocket(port, host));
    }
}
