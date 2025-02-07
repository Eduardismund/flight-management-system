package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to find a flight.
 */
@XmlRootElement(name = "findFlightCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindFlightCommand {
    @XmlAttribute
    private String number;
}
