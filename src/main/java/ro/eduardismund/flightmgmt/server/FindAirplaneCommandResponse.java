package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import ro.eduardismund.flightmgmt.server.dtos.AirplaneItem;

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
