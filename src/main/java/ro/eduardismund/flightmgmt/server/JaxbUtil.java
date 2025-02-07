package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;

public class JaxbUtil {
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(
                    CreateAirplaneCommand.class,
                    CreateAirplaneResponse.class,
                    CreateFlightCommand.class,
                    CreateFlightResponse.class,
                    GetFlightsCommand.class,
                    GetFlightsResponse.class,
                    GetAirplanesCommand.class,
                    GetAirplanesResponse.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Marshaller createMarshaller() {
        final var mar = JAXB_CONTEXT.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        return mar;
    }

    public static Unmarshaller createUnmarshaller() throws JAXBException {
        return JAXB_CONTEXT.createUnmarshaller();
    }
}
