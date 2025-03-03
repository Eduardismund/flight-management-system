package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Represents a flight containing a number and a company.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FlightItem {
    @XmlAttribute
    private String number;

    @XmlAttribute
    private String company;
}
