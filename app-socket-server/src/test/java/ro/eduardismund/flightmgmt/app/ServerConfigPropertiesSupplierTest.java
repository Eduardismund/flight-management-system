package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;

class ServerConfigPropertiesSupplierTest {

    @Test
    void get() {
        final var mockAppContext = mock(ApplicationContext.class);
        final var mockEnv = mock(Environment.class);
        doReturn(mockEnv).when(mockAppContext).getEnvironment();
        final var port = "6000";
        doReturn(port).when(mockEnv).getProperty("serverSocket.port", "6000");
        final var host = "localhost";
        doReturn(host).when(mockEnv).getProperty("serverSocket.host", "localhost");

        final var subject = new ServerConfigPropertiesSupplier(mockAppContext);
        final var config = subject.get();
        assertEquals(
                ServerConfigProperties.builder()
                        .port(Integer.parseInt(port))
                        .host(host)
                        .build(),
                config);
    }
}
