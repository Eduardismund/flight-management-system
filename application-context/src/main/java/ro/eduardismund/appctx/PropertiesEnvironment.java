package ro.eduardismund.appctx;

import java.util.Properties;
import lombok.RequiredArgsConstructor;

/**
 * {@link Environment} implementation that retrieves properties from a {@link Properties} object.
 */
@RequiredArgsConstructor
public class PropertiesEnvironment implements Environment {
    private final Properties properties;

    @Override
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
