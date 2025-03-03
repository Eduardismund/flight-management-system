package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to find a scheduled flight.
 */
@XmlRootElement(name = "findScheduledFlightCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindScheduledFlightCommand {
    @XmlAttribute
    private String number;

    @XmlAttribute
    private String departureDate;
}
