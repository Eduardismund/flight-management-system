package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AirplaneItem {
    @XmlAttribute
    private String idNumber;

    @XmlAttribute
    private String model;

    @XmlElement
    private SeatingChartDto seatingChart;
}
