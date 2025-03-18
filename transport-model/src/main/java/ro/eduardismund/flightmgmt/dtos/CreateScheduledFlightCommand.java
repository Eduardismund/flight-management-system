package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Represents a command to create a scheduled flight.
 */
@XmlRootElement(name = "createScheduledFlightCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateScheduledFlightCommand {
    @XmlAttribute
    private String flightId;

    @XmlAttribute
    private String airplane;

    @XmlAttribute
    private String departure;

    @XmlAttribute
    private String arrival;
}
