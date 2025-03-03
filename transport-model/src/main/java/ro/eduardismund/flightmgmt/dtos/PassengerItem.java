package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Data;

/**
 * Represents a passenger containing a first name, last name and an idDocument.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PassengerItem {
    @XmlAttribute
    private String firstName;

    @XmlAttribute
    private String lastName;

    @XmlAttribute
    private String idDocument;
}
