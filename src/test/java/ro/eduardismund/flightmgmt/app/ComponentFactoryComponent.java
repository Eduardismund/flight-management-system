package ro.eduardismund.flightmgmt.app;

import java.util.Properties;

public class ComponentFactoryComponent implements ComponentFactory<MultipleParametersComponent> {
    @Override
    public MultipleParametersComponent createComponent(Properties properties, ComponentResolver resolver) {
        return new MultipleParametersComponent(
                resolver.resolveComponent(ParentComponent.class), resolver.resolveComponent(ChildComponent.class));
    }
}
