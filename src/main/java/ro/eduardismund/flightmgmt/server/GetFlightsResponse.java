package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Singular;

@XmlRootElement(name = "getFlightsResponse")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class GetFlightsResponse {
    @Singular
    @XmlElementWrapper(name = "flights")
    @XmlElement(name = "flight")
    private List<FlightItem> flights = new ArrayList<>();

    @XmlAttribute
    private boolean success;
}
