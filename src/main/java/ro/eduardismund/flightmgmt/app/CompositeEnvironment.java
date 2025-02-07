package ro.eduardismund.flightmgmt.app;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * A composite environment that delegates property resolution to a list of
 * other {@link Environment} instances.
 */
@AllArgsConstructor
@NoArgsConstructor
public class CompositeEnvironment implements Environment {

    /**
     * List of environments to delegate the property resolution.
     */
    List<? extends Environment> environments;

    /**
     * Resolves the property value by checking all environments in order.
     * The first non-null value encountered is returned.
     *
     * @param propertyName The name of the property to retrieve.
     * @return The value of the property, or {@code null} if not found.
     */
    @Override
    public String getProperty(String propertyName) {
        return environments.stream()
                .map(environment -> environment.getProperty(propertyName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
