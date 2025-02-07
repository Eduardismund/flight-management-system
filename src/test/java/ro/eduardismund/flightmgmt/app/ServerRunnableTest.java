package ro.eduardismund.flightmgmt.app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.server.Server;

class ServerRunnableTest {

    @Test
    void run() {
        final var server = mock(Server.class);
        final var repo = mock(FlightManagementRepository.class);

        final var subject = new ServerRunnable(server, repo);
        final var args = new String[] {};
        subject.run(args);

        verify(server).start();
        verify(repo).init();
    }
}
