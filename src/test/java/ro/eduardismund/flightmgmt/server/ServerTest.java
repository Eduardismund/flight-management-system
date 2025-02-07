package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class ServerTest {
    private ServerConfigProperties config;
    private FlightManagementService service;
    private DomainMapper domainMapper;
    private XmlManager xmlManager;
    private CliManager cliManager;
    private Server subject;

    @BeforeEach
    void setUp() {
        config = mock(ServerConfigProperties.class);
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
        xmlManager = mock(XmlManager.class);
        cliManager = mock(CliManager.class);
        subject = spy(new Server(config, service, domainMapper, xmlManager, cliManager));
    }

    @SuppressWarnings("unchecked")
    @Test
    void runWhile_isSuccessful() throws IOException {
        final var mockSocket = mock(ServerSocket.class);
        int port = 8080;
        doReturn(port).when(config).getPort();
        doReturn(mockSocket).when(subject).getServerSocket(port);

        final var predicate = mock(Supplier.class);
        when(predicate.get()).thenReturn(true, false);

        final var client = mock(Socket.class);
        doReturn(client).when(mockSocket).accept();
        doReturn(InetAddress.getByName("127.0.0.1")).when(client).getInetAddress();
        doNothing().when(subject).listenForClient(any());

        subject.runWhile(predicate);

        verify(subject).listenForClient(client);
        verify(cliManager).println("New client connected: " + client.getInetAddress());
        verify(cliManager).println("Server started on port " + port);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Test
    void runWhile_isNotSuccessful() {

        int port = 8080;
        doReturn(port).when(config).getPort();
        final var error = new IOException("error");
        doThrow(error).when(subject).getServerSocket(port);

        final var predicate = mock(Supplier.class);
        when(predicate.get()).thenReturn(true, false);

        subject.runWhile(predicate);

        verify(cliManager).printException(same(error));
    }

    @SneakyThrows
    @Test
    void start() {
        doNothing().when(subject).runWhile(any());
        subject.start();
        verify(subject).runWhile(Server.AlwaysTrueSupplier.ALWAYS_TRUE_SUPPLIER);
    }

    @Test
    void getServerSocket_isSuccessful() throws IOException {
        final var socket = subject.getServerSocket(1234);
        assertNotNull(socket);
        assertEquals(socket.getLocalPort(), 1234);
    }

    @Test
    void listenForClient() {
        final var thread = mock(Thread.class);
        final var clientSocket = mock(Socket.class);
        doReturn(thread).when(subject).creatClientHandlerThread(clientSocket);
        subject.listenForClient(clientSocket);

        verify(thread).start();
    }

    @Test
    void createClientHandlerThread() {
        final var clientSocket = mock(Socket.class);

        final var thread = subject.creatClientHandlerThread(clientSocket);

        assertNotNull(thread);
        final var clientHandler = assertInstanceOf(ClientHandler.class, thread);
        assertSame(clientHandler.clientSocket, clientSocket);
        assertSame(clientHandler.cliManager, cliManager);
        assertSame(clientHandler.xmlManager, xmlManager);
        assertSame(clientHandler.domainMapper, domainMapper);
        assertSame(clientHandler.service, service);
    }

    @Test
    void alwaysTrueSupplier() {
        assertEquals(true, Server.AlwaysTrueSupplier.ALWAYS_TRUE_SUPPLIER.get());
    }
}
