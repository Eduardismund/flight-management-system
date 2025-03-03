package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to create an airplane.
 */
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
