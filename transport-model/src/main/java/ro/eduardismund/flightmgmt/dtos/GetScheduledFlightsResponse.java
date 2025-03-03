package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Singular;

/**
 * Represents the response containing a list of scheduled flights.
 * This class is annotated with JAXB annotations to support XML binding for serialization and deserialization.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement(name = "scheduledFlightsResponse")
public class GetScheduledFlightsResponse {

    /**
     * A list of scheduled flights returned in the response.
     * The list is wrapped inside an XML element with the name "scheduledFlights".
     */
    @Singular
    @XmlElementWrapper(name = "scheduledFlights")
    @XmlElement(name = "scheduledFlights")
    private List<ScheduledFlightItem> scheduledFlights = new ArrayList<>();

    /**
     * A boolean flag indicating whether the response was successful.
     * This attribute is serialized as an XML attribute.
     */
    @XmlAttribute
    private boolean success;
}
