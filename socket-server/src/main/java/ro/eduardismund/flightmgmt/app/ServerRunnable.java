package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import ro.eduardismund.appctx.ApplicationRunnable;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.server.Server;

/**
 * {@link ApplicationRunnable} implementation that starts the server and initializes the repository.
 */
@RequiredArgsConstructor
public class ServerRunnable implements ApplicationRunnable {
    private final Server server;
    private final FlightManagementRepository repository;

    /**
     * Initializes the repository and starts the server.
     *
     * @param args Command-line arguments.
     */
    @Override
    public void run(String[] args) {
        repository.init();
        server.start();
    }
}
