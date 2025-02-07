package ro.eduardismund.flightmgmt.server;

import java.io.*;
import java.net.*;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

@RequiredArgsConstructor
public class Server {
    private final ServerConfigProperties config;
    private final FlightManagementService service;
    private final DomainMapper domainMapper;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            System.out.println("Server started on port " + config.getPort());

            //noinspection InfiniteLoopStatement
            while (true) { // Infinite loop to accept clients
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle each client in a new thread
                new ClientHandler(service, domainMapper, clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
