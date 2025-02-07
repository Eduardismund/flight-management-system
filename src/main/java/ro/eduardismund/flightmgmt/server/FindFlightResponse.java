package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import ro.eduardismund.flightmgmt.server.dtos.FlightItem;

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
    private boolean success;
}
