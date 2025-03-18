package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for finding a flight.
 */
@XmlRootElement(name = "findFlightResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindFlightResponse {
    @XmlElement
    private FlightItem flight;

    @XmlAttribute
    private boolean found;
}
