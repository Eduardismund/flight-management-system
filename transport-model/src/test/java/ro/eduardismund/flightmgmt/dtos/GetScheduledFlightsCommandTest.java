package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class GetScheduledFlightsCommandTest {

    @Test
    void testUnmarshal() throws JAXBException {
        final var context = JAXBContext.newInstance(GetScheduledFlightsCommand.class);
        final var unmar = context.createUnmarshaller();
        assertInstanceOf(
                GetScheduledFlightsCommand.class, unmar.unmarshal(new StringReader("<getScheduledFlightCommand/>")));
    }
}
