package ro.eduardismund.flightmgmt.server.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Represents a scheduled flight containing a flight item, airplane item, departure time and arrival time.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ScheduledFlightItem {

    @XmlElement
    private FlightItem flight;

    @XmlElement
    private AirplaneItem airplane;

    @XmlAttribute
    private String departureTime;

    @XmlAttribute
    private String arrivalTime;
}
