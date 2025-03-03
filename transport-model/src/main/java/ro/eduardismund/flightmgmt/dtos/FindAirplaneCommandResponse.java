package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for finding an airplane.
 */
@XmlRootElement(name = "findAirplaneResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FindAirplaneCommandResponse {
    @XmlElement
    private AirplaneItem airplaneItem;

    @XmlAttribute
    private boolean found;
}
