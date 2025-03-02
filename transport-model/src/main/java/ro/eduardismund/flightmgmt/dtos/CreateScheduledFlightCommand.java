package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Represents a command to create a scheduled flight.
 */
@XmlRootElement(name = "createScheduledFlightCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateScheduledFlightCommand {
    @XmlElement
    private String flightId;

    @XmlElement
    private String airplane;

    @XmlAttribute
    private String departure;

    @XmlAttribute
    private String arrival;
}
