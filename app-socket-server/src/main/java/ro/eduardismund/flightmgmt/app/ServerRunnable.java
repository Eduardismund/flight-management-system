package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.server.Server;

/**
 * Starts the server.
 */
@RequiredArgsConstructor
public class ServerRunnable {
    private final Server server;

    /**
     * Starts the server.
     *
     * @param args Command-line arguments.
     */
    @SuppressWarnings("unused")
    public void run(String... args) {
        server.start();
    }
}
