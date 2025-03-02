package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class GetAirplanesCommandTest {

    @Test
    void testUnmarshal() throws JAXBException {
        final var context = JAXBContext.newInstance(GetAirplanesCommand.class);
        final var unmar = context.createUnmarshaller();
        assertInstanceOf(GetAirplanesCommand.class, unmar.unmarshal(new StringReader("<getAirplanesCommand/>")));
    }
}
