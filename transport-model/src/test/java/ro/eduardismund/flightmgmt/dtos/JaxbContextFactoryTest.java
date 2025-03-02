package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.xml.bind.JAXBException;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JaxbContextFactoryTest {

    @Test
    void createJaxbContext_success() {
        assertNotNull(JaxbContextFactory.createJaxbContext(CreateBookingCommand.class));
    }

    @Test
    void createJaxbContext_error() {
        assertThrows(JAXBException.class, () -> JaxbContextFactory.createJaxbContext(Function.class));
    }
}
