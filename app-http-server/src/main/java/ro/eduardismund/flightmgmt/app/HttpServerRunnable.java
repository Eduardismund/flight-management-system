package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ro.eduardismund.flightmgmt.server.HttpServer;

/**
 * Starts the server.
 */
@RequiredArgsConstructor
@Log
public class HttpServerRunnable {
    final HttpServer server;

    /**
     * Starts the server.
     *
     * @param args CLI arguments (not used)
     */
    @SuppressWarnings("unused")
    public void run(String... args) {
        log.info("Starting HttpServer");
        server.start();
    }
}
