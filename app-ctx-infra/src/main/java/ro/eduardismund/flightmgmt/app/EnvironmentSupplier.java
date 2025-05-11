package ro.eduardismund.flightmgmt.app;

import java.io.IOException;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * Supplier that provides an environment for Spring ApplicationContext.
 */
public class EnvironmentSupplier implements Supplier<ConfigurableEnvironment> {
    /**
     * Get the {@link ConfigurableEnvironment}.
     *
     * @return an instance of  {@link ConfigurableEnvironment}.
     */
    @SneakyThrows
    @Override
    public ConfigurableEnvironment get() {
        final var environment = new StandardEnvironment();
        environment.getPropertySources().addLast(createResourcePropertySource());
        return environment;
    }

    /**
     * Creates a {@link ResourcePropertySource} reading properties from an 'application.properties' file.
     *
     * @return a {@link ResourcePropertySource} instance
     *
     * @throws IOException if an I/O error occurs
     */
    ResourcePropertySource createResourcePropertySource() throws IOException {
        return new ResourcePropertySource("file:./config/application.properties");
    }
}
