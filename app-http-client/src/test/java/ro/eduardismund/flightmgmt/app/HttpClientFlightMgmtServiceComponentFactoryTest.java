package ro.eduardismund.flightmgmt.app;

import org.junit.jupiter.api.Test;
import ro.eduardismund.appctx.ComponentResolver;
import ro.eduardismund.appctx.Environment;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;

import java.net.URISyntaxException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class HttpClientFlightMgmtServiceComponentFactoryTest {
    private final HttpClientFlightMgmtServiceComponentFactory subject = new HttpClientFlightMgmtServiceComponentFactory();


    @Test
    void createComponent_isSuccessful() {
        final var mockProperties = mock(Environment.class);
        final var mockResolver = mock(ComponentResolver.class);

        doReturn("http://localhost:8080").when(mockProperties).getProperty("restApiBaseUri");
        final var mockXmlManager = mock(XmlManager.class);
        final var mockDomainMapper = mock(DomainMapper.class);
        doReturn(mockXmlManager).when(mockResolver).resolveComponent(XmlManager.class);
        doReturn(mockDomainMapper).when(mockResolver).resolveComponent(DomainMapper.class);

        //act

        final var actual = subject.createComponent(mockProperties, mockResolver);

        assertNotNull(actual);

    }

    @Test
    void createComponent_throwsException() {
        final var mockProperties = mock(Environment.class);
        final var mockResolver = mock(ComponentResolver.class);

        doReturn("invalid uri").when(mockProperties).getProperty("restApiBaseUri");

        assertThrows(URISyntaxException.class, () -> subject.createComponent(mockProperties, mockResolver));
    }
}