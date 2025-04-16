package ro.eduardismund.flightmgmt.app;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

/**
 * Initializer for an implementation of {@link FlightManagementRepository} on ApplicationContext refresh.
 */
public class FlightMgmtRepositoryInitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * Initializes the {@link FlightManagementRepository} found as a bean in ApplicationContext.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(FlightManagementRepository.class).init();
    }
}
