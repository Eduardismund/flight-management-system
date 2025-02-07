package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import ro.eduardismund.flightmgmt.server.dtos.ScheduledFlightItem;

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
