package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for creating an airplane.
 */
@XmlRootElement(name = "createAirplaneResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateAirplaneResponse {
    @XmlAttribute
    private String airplaneId;

    @XmlElement
    private boolean success;

    @XmlElement
    private CarErrorType error;

    /**
     * Enum to represent error types during airplane creation.
     */
    @SuppressWarnings({"PMD.FieldNamingConventions", "PMD.LongVariable"})
    public enum CarErrorType {
        AirplaneAlreadyExists,
        InternalError
    }
}
