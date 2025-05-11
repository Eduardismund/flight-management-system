package ro.eduardismund.flightmgmt.app;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import ro.eduardismund.flightmgmt.server.HttpServerProperties;

/**
 * Supplier that provides a {@link HttpServerProperties} instance.
 */
@RequiredArgsConstructor
public class HttpServerPropertiesSupplier implements Supplier<HttpServerProperties> {
    private final ApplicationContext applicationContext;

    /**
     * Creates a new {@link HttpServerProperties} instance with given port from {@link ApplicationContext} properties.
     *
     * @return a {@link HttpServerProperties} instance
     */
    @Override
    public HttpServerProperties get() {
        return HttpServerProperties.builder()
                .port(applicationContext.getEnvironment().getProperty("serverHttp.port", Integer.class, 8080))
                .build();
    }
}
