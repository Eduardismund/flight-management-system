package ro.eduardismund.flightmgmt.app;

import java.util.Properties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PropertiesEnvironment implements Environment {
    private final Properties properties;

    @Override
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
