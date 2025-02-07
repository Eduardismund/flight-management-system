package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "createAirplaneCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateAirplaneCommand {
    @XmlAttribute
    private String idNumber;

    @XmlAttribute
    private String model;

    @XmlElement
    private SeatingChartDto seatingChart;
}
