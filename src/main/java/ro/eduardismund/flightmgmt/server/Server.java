package ro.eduardismund.flightmgmt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

/**
 * Represents a server that listens for incoming client connections and handles them.
 * The server runs in an infinite loop until stopped.
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@RequiredArgsConstructor
public class Server {

    private final ServerConfigProperties config;
    private final FlightManagementService service;
    private final DomainMapper domainMapper;
    private final XmlManager xmlManager;
    private final CliManager cliManager;

    /**
     * Starts the server and continuously listens for client connections.
     * The server will keep running based on the condition defined in the predicate.
     */
    public void start() {
        runWhile(AlwaysTrueSupplier.ALWAYS_TRUE_SUPPLIER);
    }

    /**
     * Runs the server in an infinite loop while the given predicate returns true.
     * Accepts client connections and listens for client requests.
     *
     * @param predicate The condition that controls the server loop.
     */
    void runWhile(Supplier<Boolean> predicate) {
        try (var serverSocket = getServerSocket(config.getPort())) {
            cliManager.println("Server started on port " + config.getPort());
            while (predicate.get()) { // Infinite loop to accept clients
                try (Socket clientSocket = serverSocket.accept()) {
                    cliManager.println("New client connected: " + clientSocket.getInetAddress());
                    listenForClient(clientSocket);
                }
            }
        } catch (IOException e) {
            cliManager.printException(e);
        }
    }

    /**
     * Creates a new server socket that listens on the specified port.
     *
     * @param port The port on which the server will listen.
     * @return The created ServerSocket.
     * @throws IOException If an error occurs while creating the server socket.
     */
    ServerSocket getServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    /**
     * Listens for client connections by creating a new client handler thread.
     *
     * @param clientSocket The socket representing the client connection.
     */
    void listenForClient(Socket clientSocket) {
        // Handle each client in a new thread
        creatClientHandlerThread(clientSocket).start();
    }

    /**
     * Creates a new thread to handle a client connection.
     *
     * @param clientSocket The socket representing the client connection.
     * @return A new thread responsible for handling the client.
     */
    Thread creatClientHandlerThread(Socket clientSocket) {
        return new ClientHandler(service, domainMapper, clientSocket, xmlManager, cliManager);
    }

    /**
     * A supplier that always returns true. Used to keep the server running indefinitely.
     */
    enum AlwaysTrueSupplier implements Supplier<Boolean> {
        ALWAYS_TRUE_SUPPLIER;

        @Override
        public Boolean get() {
            return true;
        }
    }
}
