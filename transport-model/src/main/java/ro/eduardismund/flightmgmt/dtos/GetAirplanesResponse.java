package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Singular;

/**
 * Response containing a list of airplanes.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getAirplanesResponse")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAirplanesResponse {
    @Singular
    @XmlElementWrapper(name = "airplane")
    @XmlElement(name = "airplane")
    private List<AirplaneItem> airplanes = new ArrayList<>();

    @XmlAttribute
    private boolean success;
}
