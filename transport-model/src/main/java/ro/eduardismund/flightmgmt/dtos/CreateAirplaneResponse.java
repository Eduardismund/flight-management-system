package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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

    @XmlAttribute
    private boolean success;

    @XmlAttribute
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
