package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to create an airplane.
 */
@XmlRootElement(name = "createFlightCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateFlightCommand {
    @XmlAttribute
    private String number;

    @XmlAttribute
    private String company;
}
