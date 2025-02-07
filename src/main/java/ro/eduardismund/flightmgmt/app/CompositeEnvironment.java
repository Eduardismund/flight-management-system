package ro.eduardismund.flightmgmt.app;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeEnvironment implements Environment {
    private final List<? extends Environment> environments;

    @Override
    public String getProperty(String propertyName) {
        return environments.stream()
                .map(environment -> environment.getProperty(propertyName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
