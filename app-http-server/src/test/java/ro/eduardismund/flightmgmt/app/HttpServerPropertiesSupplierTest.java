package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import ro.eduardismund.flightmgmt.server.HttpServerProperties;

class HttpServerPropertiesSupplierTest {

    private final ApplicationContext appCtx = mock(ApplicationContext.class);
    private final Environment env = mock(Environment.class);
    private final HttpServerPropertiesSupplier subject = new HttpServerPropertiesSupplier(appCtx);

    @Test
    void get() {
        doReturn(env).when(appCtx).getEnvironment();
        final var port = 8083;
        doReturn(port).when(env).getProperty("serverHttp.port", Integer.class, 8080);
        assertEquals(HttpServerProperties.builder().port(port).build(), subject.get());
    }
}
