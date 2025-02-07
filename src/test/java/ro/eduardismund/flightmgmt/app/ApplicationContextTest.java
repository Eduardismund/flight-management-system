package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationContextTest {
    ApplicationContext appCon;

    @BeforeEach
    void setUp() {
        appCon = new ApplicationContext();
    }

    @SneakyThrows
    @Test
    void putComponent_throwsException() {
        appCon = spy(appCon);
        final var instantiationEx = new InstantiationException();
        doThrow(instantiationEx).when(appCon).callConstructor(any());
        assertEquals(
                instantiationEx, assertThrows(InstantiationException.class, () -> appCon.putComponent(any(), any())));
    }

    @SneakyThrows
    @Test
    void callConstructor_throwsException() {
        final var params = new Parameter[] {};
        final var constructor = mock(Constructor.class);
        doReturn(1).when(constructor).getParameterCount();
        doReturn(params).when(constructor).getParameters();
        final var instantiationEx = new InstantiationException();
        doThrow(instantiationEx).when(constructor).newInstance(any());

        assertEquals(
                instantiationEx, assertThrows(InstantiationException.class, () -> appCon.callConstructor(constructor)));
    }

    @Test
    void registerComponent() {
        final var component = new ChildComponent();

        appCon.registerComponent(component);

        assertEquals(component, appCon.components.get(component.getClass()));
        assertEquals(1, appCon.components.size());
    }

    @Test
    void registerComponentClass_withCondition() {
        final var condition = mock(Condition.class);
        appCon.registerComponentClass(ChildComponent.class, condition);

        assertEquals(
                Set.of(new ApplicationContext.ComponentCondition(ChildComponent.class, condition)),
                appCon.componentClasses);
    }

    @Test
    void registerComponentClass_alwaysTrue() {
        appCon.registerComponentClass(ChildComponent.class);

        assertEquals(
                Set.of(new ApplicationContext.ComponentCondition(ChildComponent.class, Condition.alwaysTrue())),
                appCon.componentClasses);
    }

    @Test
    void run_isInstance() {

        final var appRunnable1 = mock(ApplicationRunnable.class);
        final var appRunnable2 = mock(ApplicationRunnable.class);
        appCon.components.put(ApplicationContext.class, appRunnable1);
        appCon.components.put(ChildComponent.class, appRunnable2);

        final String[] args = {"arg1"};
        appCon.run(args);

        verify(appRunnable1).run(same(args));
        verify(appRunnable2).run(same(args));
    }

    @Test
    void run_isNotInstance() {
        final var component1 = mock(ChildComponent.class);

        appCon.components.put(ChildComponent.class, component1);

        String[] args = {"arg1"};
        appCon.run(args);
        verifyNoInteractions(component1);
    }

    @Test
    void loadConfigFileTest() throws IOException {
        var sampleProperties = "key1=value1\nkey2=value2";
        var buffer = new BufferedReader(new StringReader(sampleProperties));

        appCon = spy(ApplicationContext.class);

        doReturn(buffer).when(appCon).getReader();

        final var properties = appCon.loadConfigFile();

        assertEquals("value1", properties.getProperty("key1"));
        assertEquals("value2", properties.getProperty("key2"));
    }

    @Test
    void loadConfigFileTest_throwsException() throws IOException {

        appCon = spy(ApplicationContext.class);

        final var ioException = new IOException();
        doThrow(ioException).when(appCon).getReader();

        assertSame(ioException, assertThrows(IOException.class, appCon::loadConfigFile));
    }

    @Test
    void resolveDependencies_containsKey() {
        final var component1 = new ChildComponent();

        appCon.components.put(ChildComponent.class, component1);

        assertSame(component1, appCon.resolveDependency(ChildComponent.class));
    }

    @Test
    void resolveDependencies_isAssignableFromClass() {
        final var component1 = new ChildComponent();

        appCon.components.put(ChildComponent.class, component1);

        assertSame(component1, appCon.resolveDependency(ParentComponent.class));
    }

    @Test
    void resolveDependencies_isAssignableFromInterface() {
        final var component1 = new ChildComponent();

        appCon.components.put(ChildComponent.class, component1);

        assertSame(component1, appCon.resolveDependency(InterfaceComponent.class));
    }

    @Test
    void resolveDependencies_isNotAssignableFrom() {
        final var component1 = new ParentComponent();
        appCon.components.put(ParentComponent.class, component1);

        final var exception =
                assertThrows(IllegalStateException.class, () -> appCon.resolveDependency(ChildComponent.class));

        assertEquals("Could not resolve dependency of type " + ChildComponent.class.getName(), exception.getMessage());
    }

    @Test
    void resolveDependencies_createdFromComponentClass() {
        final var component1 = new ParentComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(ChildComponent.class, Condition.alwaysTrue()));

        final var childComponent = appCon.resolveDependency(ChildComponent.class);

        assertNotNull(childComponent);
        assertEquals(2, appCon.components.size());
        assertSame(childComponent, appCon.components.get(ChildComponent.class));
    }

    @Test
    void resolveDependencies_withComponentClasses_conditionNotMet() {
        final var component1 = new ParentComponent();

        final var condition = createFalseCondition();

        appCon.components.put(ParentComponent.class, component1);
        appCon.componentClasses.add(new ApplicationContext.ComponentCondition(ChildComponent.class, condition));

        final var exception =
                assertThrows(IllegalStateException.class, () -> appCon.resolveDependency(ChildComponent.class));

        assertEquals("Could not resolve dependency of type " + ChildComponent.class.getName(), exception.getMessage());
    }

    private static Condition createFalseCondition() {
        final var condition = mock(Condition.class);
        doReturn(false).when(condition).test(any());
        return condition;
    }

    @Test
    void resolveDependencies_withComponentClasses_unresolvable() {

        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(ParentComponent.class, Condition.alwaysTrue()));

        final var exception =
                assertThrows(IllegalStateException.class, () -> appCon.resolveDependency(ChildComponent.class));

        assertEquals("Could not resolve dependency of type " + ChildComponent.class.getName(), exception.getMessage());
    }

    @Test
    void resolveDependencies_multipleConstructors() {
        final var component1 = new ParentComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(MultipleConstructorsComponent.class, Condition.alwaysTrue()));

        final var component = appCon.resolveDependency(MultipleConstructorsComponent.class);

        assertNotNull(component);
        assertSame(component, appCon.components.get(MultipleConstructorsComponent.class));
    }

    @Test
    void resolveDependencies_multipleParameters() {
        final var component1 = new ParentComponent();
        final var component2 = new ChildComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.components.put(ChildComponent.class, component2);

        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(MultipleParametersComponent.class, Condition.alwaysTrue()));

        final var component = appCon.resolveDependency(MultipleParametersComponent.class);

        assertNotNull(component);
        assertSame(component, appCon.components.get(MultipleParametersComponent.class));
    }

    @Test
    void processComponents_instanceOfComponentFactory() {
        appCon = spy(ApplicationContext.class);
        doReturn(new PropertiesEnvironment(new Properties())).when(appCon).loadConfigFile();

        final var component1 = new ParentComponent();
        final var component2 = new ChildComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.components.put(ChildComponent.class, component2);

        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(DepOfMultipleParamsComponent.class, Condition.alwaysTrue()));
        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(ComponentFactoryComponent.class, Condition.alwaysTrue()));

        appCon.processComponents();

        final var compByFactory = appCon.components.get(MultipleParametersComponent.class);
        assertNotNull(compByFactory);
        final var depOnCompByFactory =
                (DepOfMultipleParamsComponent) appCon.components.get(DepOfMultipleParamsComponent.class);
        assertNotNull(depOnCompByFactory);
        assertSame(compByFactory, depOnCompByFactory.dep);
    }

    @Test
    void processComponents_instanceOfComponentFactory_conditionNotMet() {
        appCon = spy(ApplicationContext.class);
        doReturn(new PropertiesEnvironment(new Properties())).when(appCon).loadConfigFile();

        final var component1 = new ParentComponent();
        final var component2 = new ChildComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.components.put(ChildComponent.class, component2);

        final var condition = createFalseCondition();

        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(ComponentFactoryComponent.class, condition));

        appCon.processComponents();

        final var compByFactory = appCon.components.get(MultipleParametersComponent.class);
        assertNull(compByFactory);
    }

    @Test
    void processComponents_privateConstructorOnly() {
        final var component1 = new ParentComponent();

        appCon.components.put(ParentComponent.class, component1);
        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(PrivateConstructorComponent.class, Condition.alwaysTrue()));

        final var exception = assertThrows(IllegalStateException.class, appCon::processComponents);

        assertEquals("No public constructor found for " + PrivateConstructorComponent.class, exception.getMessage());
    }

    @Test
    void processComponents_privateConstructorOnly_conditionNotMet() {
        final var component1 = new ParentComponent();

        final var condition = createFalseCondition();

        appCon.components.put(ParentComponent.class, component1);
        appCon.componentClasses.add(
                new ApplicationContext.ComponentCondition(PrivateConstructorComponent.class, condition));
        appCon.processComponents();
        assertNull(appCon.components.get(PrivateConstructorComponent.class));
    }
}
