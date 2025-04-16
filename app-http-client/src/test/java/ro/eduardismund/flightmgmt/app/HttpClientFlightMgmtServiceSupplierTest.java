package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;

class HttpClientFlightMgmtServiceSupplierTest {
    private final ApplicationContext mockAppCtx = mock(ApplicationContext.class);
    private final HttpClientFlightMgmtServiceSupplier subject = new HttpClientFlightMgmtServiceSupplier(mockAppCtx);

    @Test
    void createComponent_isSuccessful() {
        final var mockEnv = mock(Environment.class);
        doReturn(mockEnv).when(mockAppCtx).getEnvironment();

        doReturn("http://localhost:8080").when(mockEnv).getRequiredProperty("restApiBaseUri");
        final var mockXmlManager = mock(XmlManager.class);
        final var mockDomainMapper = mock(DomainMapper.class);
        doReturn(mockXmlManager).when(mockAppCtx).getBean(XmlManager.class);
        doReturn(mockDomainMapper).when(mockAppCtx).getBean(DomainMapper.class);

        // act

        final var actual = subject.get();

        assertNotNull(actual);
    }

    @Test
    void createComponent_throwsException() {
        final var mockEnv = mock(Environment.class);
        doReturn(mockEnv).when(mockAppCtx).getEnvironment();

        doReturn("invalid uri").when(mockEnv).getRequiredProperty("restApiBaseUri");

        assertThrows(URISyntaxException.class, subject::get);
    }
}
