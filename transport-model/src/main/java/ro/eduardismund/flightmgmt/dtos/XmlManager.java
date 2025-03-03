package ro.eduardismund.flightmgmt.dtos;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.Writer;
import lombok.SneakyThrows;

/**
 * The {@link XmlManager} class provides methods for marshalling and unmarshalling Java objects
 * to and from XML using JAXB (Java Architecture for XML Binding).
 * This class facilitates the serialization and deserialization of specific command and response objects.
 */
public class XmlManager {

    /**
     * An array of classes to be used in JAXB context for marshalling/unmarshalling.
     * These are the command and response objects that will be serialized or deserialized.
     */
    @SuppressFBWarnings("MS_PKGPROTECT")
    public static final Class<?>[] CLASSES = {
        CreateAirplaneCommand.class,
        CreateAirplaneResponse.class,
        CreateFlightCommand.class,
        CreateFlightResponse.class,
        CreateBookingCommand.class,
        CreateBookingResponse.class,
        CreateScheduledFlightCommand.class,
        CreateScheduledFlightResponse.class,
        FindFlightCommand.class,
        FindFlightResponse.class,
        FindAirplaneCommand.class,
        FindAirplaneCommandResponse.class,
        FindScheduledFlightCommand.class,
        FindScheduledFlightResponse.class,
        GetFlightsCommand.class,
        GetFlightsResponse.class,
        GetAirplanesCommand.class,
        GetAirplanesResponse.class,
        GetScheduledFlightsCommand.class,
        GetScheduledFlightsResponse.class
    };

    private final JAXBContext jaxbContext;

    /**
     * Default constructor that initializes the JAXBContext with the predefined classes.
     */
    public XmlManager() {
        this(JaxbContextFactory.createJaxbContext(CLASSES));
    }

    /**
     * Constructor to initialize {@link JAXBContext} with a custom context.
     *
     * @param jaxbContext The JAXBContext to be used for marshalling and unmarshalling.
     */
    XmlManager(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    /**
     * Creates and configures a JAXB marshaller for converting Java objects to XML.
     *
     * @return The configured {@link Marshaller} object.
     */
    @SneakyThrows
    Marshaller createMarshaller() {
        final var mar = jaxbContext.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        return mar;
    }

    /**
     * Creates and configures a JAXB unmarshaller for converting XML to Java objects.
     *
     * @return The configured {@link Unmarshaller} object.
     */
    @SneakyThrows
    Unmarshaller createUnmarshaller() {
        return jaxbContext.createUnmarshaller();
    }

    /**
     * Marshals the given object into XML and writes it to the provided {@link Writer}.
     *
     * @param object The object to be marshalled to XML.
     * @param writer The {@link Writer} to which the XML will be written.
     */
    @SneakyThrows
    public void marshal(Object object, Writer writer) {
        createMarshaller().marshal(object, writer);
    }

    /**
     * Unmarshals XML from the provided {@link Reader} into a Java object.
     *
     * @param reader The {@link Reader} from which the XML is read.
     * @return The Java object resulting from the unmarshalling process.
     */
    @SneakyThrows
    public Object unmarshal(Reader reader) {
        return createUnmarshaller().unmarshal(reader);
    }
}
