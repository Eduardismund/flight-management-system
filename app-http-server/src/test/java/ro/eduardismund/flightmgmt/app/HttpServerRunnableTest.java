package ro.eduardismund.flightmgmt.app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.server.HttpServer;

class HttpServerRunnableTest {
    HttpServer server;

    @Test
    void run() {
        server = mock(HttpServer.class);
        final var subject = new HttpServerRunnable(server);
        final var args = new String[] {};
        subject.run(args);

        verify(server).start();
    }
}
