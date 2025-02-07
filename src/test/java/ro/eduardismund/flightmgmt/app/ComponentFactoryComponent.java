package ro.eduardismund.flightmgmt.app;

public class ComponentFactoryComponent implements ComponentFactory<MultipleParametersComponent> {
    @Override
    public MultipleParametersComponent createComponent(Environment properties, ComponentResolver resolver) {
        return new MultipleParametersComponent(
                resolver.resolveComponent(ParentComponent.class), resolver.resolveComponent(ChildComponent.class));
    }
}
