package ro.eduardismund.flightmgmt.app;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import ro.eduardismund.flightmgmt.repo.FlightManagementRepository;

class FlightMgmtRepositoryInitApplicationListenerTest {

    @Test
    void onApplicationEvent() {
        final var mockEvent = mock(ContextRefreshedEvent.class);
        final var mockAppContext = mock(ApplicationContext.class);
        doReturn(mockAppContext).when(mockEvent).getApplicationContext();

        final var mockRepository = mock(FlightManagementRepository.class);
        doReturn(mockRepository).when(mockAppContext).getBean(FlightManagementRepository.class);

        final var subject = new FlightMgmtRepositoryInitApplicationListener();
        subject.onApplicationEvent(mockEvent);

        verify(mockRepository).init();
    }
}
