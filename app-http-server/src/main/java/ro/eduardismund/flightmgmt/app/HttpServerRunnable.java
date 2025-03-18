package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ro.eduardismund.appctx.ApplicationRunnable;
import ro.eduardismund.flightmgmt.server.HttpServer;

/**
 * {@link ApplicationRunnable} implementation that starts the server.
 */
@RequiredArgsConstructor
@Log
public class HttpServerRunnable implements ApplicationRunnable {
    final HttpServer server;

    @Override
    public void run(String... args) {
        log.info("Starting HttpServer");
        server.start();
    }
}
