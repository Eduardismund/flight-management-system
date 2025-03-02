package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class GetFlightsCommandTest {

    @Test
    void testUnmarshal() throws JAXBException {
        final var context = JAXBContext.newInstance(GetFlightsCommand.class);
        final var unmar = context.createUnmarshaller();
        assertInstanceOf(GetFlightsCommand.class, unmar.unmarshal(new StringReader("<getFlightsCommand/>")));
    }
}
