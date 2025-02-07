package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.JAXBContext;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
class JaxbContextFactory {

    @SneakyThrows
    static JAXBContext createJaxbContext(Class<?>... classes) {
        return JAXBContext.newInstance(classes);
    }
}
