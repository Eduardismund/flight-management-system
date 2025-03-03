package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XmlManagerTest {
    private XmlManager xmlManager;

    @BeforeEach
    void setUp() {
        xmlManager = new XmlManager();
    }

    @SneakyThrows
    @Test
    void testCreateMarshaller() {
        final var mockMarshaller = mock(Marshaller.class);
        final var jaxbContextMock = mock(JAXBContext.class);

        doReturn(mockMarshaller).when(jaxbContextMock).createMarshaller();

        xmlManager = new XmlManager(jaxbContextMock);

        xmlManager.createMarshaller();
        verify(mockMarshaller).setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        verify(mockMarshaller).setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

    @Test
    void testMarshal_throwsJAXBException() throws JAXBException {
        final var jaxbContextMock = mock(JAXBContext.class);
        final var exception = new JAXBException("test");
        doThrow(exception).when(jaxbContextMock).createMarshaller();
        StringWriter writer = new StringWriter();
        CreateFlightCommand command = new CreateFlightCommand();
        xmlManager = new XmlManager(jaxbContextMock);

        assertThrows(JAXBException.class, () -> xmlManager.marshal(command, writer));
    }

    @Test
    void testUnmarshal_throwsJAXBException() throws JAXBException {
        final var jaxbContextMock = mock(JAXBContext.class);
        final var exception = new JAXBException("test");
        doThrow(exception).when(jaxbContextMock).createUnmarshaller();
        final var reader = new StringReader("123");
        xmlManager = new XmlManager(jaxbContextMock);

        assertThrows(JAXBException.class, () -> xmlManager.unmarshal(reader));
    }

    @Test
    void testMarshal() {

        CreateFlightCommand command = new CreateFlightCommand();
        command.setNumber("F123");
        command.setCompany("Tarom");

        StringWriter writer = new StringWriter();
        xmlManager.marshal(command, writer);
        String xmlOutput = writer.toString();

        assertNotNull(xmlOutput);

        String expectedXml = """
                <createFlightCommand number="F123" company="Tarom"/>""";

        assertEquals(expectedXml, xmlOutput);
    }

    @Test
    void testUnmarshal() {
        String xmlInput = """
                <createFlightCommand number="F123" company="Tarom"/>""";

        StringReader reader = new StringReader(xmlInput);
        CreateFlightCommand deserializedCommand = (CreateFlightCommand) xmlManager.unmarshal(reader);

        assertNotNull(deserializedCommand);
        assertEquals("F123", deserializedCommand.getNumber());
        assertEquals("Tarom", deserializedCommand.getCompany());
    }

    @Test
    void testUnmarshalInvalidXml() {
        String invalidXml = "<InvalidTag></InvalidTag>";

        assertThrows(Exception.class, () -> {
            StringReader reader = new StringReader(invalidXml);
            xmlManager.unmarshal(reader);
        });
    }
}
