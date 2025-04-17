package ro.eduardismund.flightmgmt.app;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ro.eduardismund.flightmgmt.server.Server;

class ServerRunnableTest {

    @Test
    void run() {
        final var server = Mockito.mock(Server.class);
        final var subject = new ServerRunnable(server);
        final var args = new String[] {};

        subject.run(args);

        verify(server).start();
    }
}
