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
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

@SuppressWarnings("unchecked")
class ServerTest {
    private ServerConfigProperties config;
    private FlightManagementService service;
    private DomainMapper domainMapper;
    private XmlManager xmlManager;
    private Logger logger;
    private Server subject;

    @BeforeEach
    void setUp() {
        config = mock(ServerConfigProperties.class);
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
        xmlManager = mock(XmlManager.class);
        logger = mock(Logger.class);
        subject = spy(new Server(config, service, domainMapper, xmlManager, logger));
    }

    @Test
    void runWhile_isSuccessful() throws IOException {
        final var mockSocket = mock(ServerSocket.class);
        int port = 8080;
        doReturn(port).when(config).getPort();
        doReturn(mockSocket).when(subject).getServerSocket(port);

        final var predicate = mock(Supplier.class);
        when(predicate.get()).thenReturn(true, false);

        doNothing().when(subject).listenForClient(mockSocket);

        subject.runWhile(predicate);

        verify(subject).listenForClient(mockSocket);
        verify(logger).println("Server started on port " + port);
    }

    @SneakyThrows
    @Test
    void runWhile_isNotSuccessful() {

        int port = 8080;
        doReturn(port).when(config).getPort();
        final var error = new IOException("error");
        doThrow(error).when(subject).getServerSocket(port);

        final var predicate = mock(Supplier.class);
        when(predicate.get()).thenReturn(true, false);

        subject.runWhile(predicate);

        verify(logger).printException(same(error));
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
        final var serverSocket = mock(ServerSocket.class);
        final var argumentCaptor = ArgumentCaptor.forClass(Server.ClientSocketSupplier.class);
        doReturn(thread).when(subject).creatClientHandlerThread(argumentCaptor.capture());
        subject.listenForClient(serverSocket);

        verify(thread).start();
        assertSame(serverSocket, argumentCaptor.getValue().serverSocket);
        assertSame(logger, argumentCaptor.getValue().cliManager);
    }

    @Test
    void createClientHandlerThread() {
        final var clientSocket = mock(Supplier.class);

        final var thread = subject.creatClientHandlerThread(clientSocket);

        assertNotNull(thread);
        final var clientHandler = assertInstanceOf(ClientHandler.class, thread);
        assertSame(clientHandler.clientSocketSupplier, clientSocket);
        assertSame(clientHandler.cliManager, logger);
        assertSame(clientHandler.xmlManager, xmlManager);
        assertSame(clientHandler.domainMapper, domainMapper);
        assertSame(clientHandler.service, service);
    }

    @Test
    void alwaysTrueSupplier() {
        assertEquals(true, Server.AlwaysTrueSupplier.ALWAYS_TRUE_SUPPLIER.get());
    }

    @Test
    void clientSocketSupplier_get() throws IOException {
        final var mockServerSocket = mock(ServerSocket.class);

        final var clientSocketMock = mock(Socket.class);
        doReturn(clientSocketMock).when(mockServerSocket).accept();
        doReturn(InetAddress.getByName("192.168.16.1")).when(clientSocketMock).getInetAddress();

        final var mockCliManager = mock(Logger.class);
        final var cliSocketSupplier = new Server.ClientSocketSupplier(mockServerSocket, mockCliManager);

        assertSame(clientSocketMock, cliSocketSupplier.get());
        verify(mockCliManager).println("New client connected: /192.168.16.1");
    }

    @Test
    void clientSocketSupplier_get_throwsIoException() throws IOException {
        final var mockServerSocket = mock(ServerSocket.class);

        final var ioException = new IOException();
        doThrow(ioException).when(mockServerSocket).accept();

        final var mockCliManager = mock(Logger.class);
        final var cliSocketSupplier = new Server.ClientSocketSupplier(mockServerSocket, mockCliManager);

        assertSame(ioException, assertThrows(IOException.class, cliSocketSupplier::get));
        verifyNoInteractions(mockCliManager);
    }
}
