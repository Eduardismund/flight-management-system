package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Singular;

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
