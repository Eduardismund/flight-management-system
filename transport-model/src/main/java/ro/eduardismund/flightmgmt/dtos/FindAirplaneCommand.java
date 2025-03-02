package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to find an airplane.
 */
@XmlRootElement(name = "findAirplaneCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindAirplaneCommand {
    @XmlAttribute
    private String number;
}
