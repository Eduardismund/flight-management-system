package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Represents an airplane containing an id number, model and a seating chart.
 */
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
