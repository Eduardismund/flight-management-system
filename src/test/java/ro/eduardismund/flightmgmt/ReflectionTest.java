package ro.eduardismund.flightmgmt;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class ReflectionTest {
    final private int INTEGER = 12;
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(classes = {Seat.class, SeatingChart.class})
    void seatInstantiation(Class<?> cls)  {
       // final var constructors = cls.getConstructors()[0];
        for(final var constructor: cls.getConstructors()) {

            Object[] parameters = new Object[constructor.getParameterCount()];
            int index = 0;

            for(final var param: constructor.getParameters()){
                parameters[index++]  = getValue(param);
            }

            Object instance = constructor.newInstance(parameters);
            Arrays.stream(cls.getMethods()).filter(ReflectionTest::methodIsGetter)
                    .forEach(method -> System.out.println(method.getName() + " " + invokeMethod(method, instance)));
        }
//        final var object = constructor.newInstance(10, "D", false);
//        System.out.println(object);



    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(classes = {Seat.class, SeatingChart.class})
    void getSuperclassesAndInterfaces(Class<?> cls)  {
//        while(cls != null) {
//            final var superclass = cls.getSuperclass();
//
//            if (superclass != null) {
//                System.out.println(superclass);
//            }
//
//            final var interfaces = cls.getInterfaces();
//
//            if (interfaces.length > 0) {
//                System.out.println(Arrays.toString(interfaces));
//            }
//            cls = cls.getSuperclass();
//        }
        explore(cls);


    }

    private void explore(Class<?> cls) {
        if(cls == null){
            return;
        }

        final var interfaces = cls.getInterfaces();

        if(interfaces.length > 0 ){
            for(final var intf: interfaces){
                System.out.println(intf.getName());
                explore(intf);
            }
        }

        final var superclass = cls.getSuperclass();

        if(superclass != null){
            System.out.println(superclass.getName());
            explore(superclass);
        }

    }


    @SneakyThrows
    private Object invokeMethod(Method method, Object o) {
        return method.invoke(o);
    }

    private static boolean methodIsGetter(Method method) {
        if (method.getName().equals("getClass")) {
            return false;
        }
        if (method.getParameterCount() != 0) {
            return false;
        }
        if (method.getReturnType().equals(boolean.class) && method.getName().startsWith("is")) {
            return true;
        }
        return method.getName().startsWith("get");
    }
}
