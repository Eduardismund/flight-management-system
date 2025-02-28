package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MultipleParametersComponent {
    public MultipleParametersComponent(ParentComponent parentComponent, ChildComponent childComponent) {
        assertNotNull(parentComponent);
        assertNotNull(childComponent);
    }
}
