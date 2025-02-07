package ro.eduardismund.flightmgmt.app;

import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;
import ro.eduardismund.flightmgmt.server.Server;

@RequiredArgsConstructor
public class ServerRunnable implements ApplicationRunnable {
    private final Server server;
    private final FlightManagementRepository repository;

    @Override
    public void run(String[] args) {
        repository.init();
        server.start();
    }
}
