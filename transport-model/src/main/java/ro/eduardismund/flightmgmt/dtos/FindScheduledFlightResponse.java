package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for finding a scheduled flight.
 */
@XmlRootElement(name = "findScheduledFlightResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindScheduledFlightResponse {
    @XmlElement
    private ScheduledFlightItem scheduledFlightItem;

    @XmlAttribute
    private boolean found;
}
