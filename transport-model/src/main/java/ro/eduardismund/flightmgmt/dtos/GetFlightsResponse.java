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
 * Response containing a list of flights.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getFlightsResponse")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetFlightsResponse {
    @Singular
    @XmlElementWrapper(name = "flights")
    @XmlElement(name = "flight")
    private List<FlightItem> flights = new ArrayList<>();

    @XmlAttribute
    private boolean success;
}
