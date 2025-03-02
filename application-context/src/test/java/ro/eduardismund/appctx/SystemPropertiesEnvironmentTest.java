package ro.eduardismund.appctx;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SystemPropertiesEnvironmentTest {

    @Test
    void getProperty() {
        final var env = SystemPropertiesEnvironment.SYS_PROP_ENV;
        assertNull(env.getProperty("non.existent.property"));
    }
}
