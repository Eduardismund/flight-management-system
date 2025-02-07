package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for creating a flight.
 */
@XmlRootElement(name = "createFlightResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateFlightResponse {
    @XmlAttribute
    private String number;

    @XmlAttribute
    private boolean success;

    @XmlAttribute
    private CfrErrorType error;

    /**
     * Enum to represent error types during flight creation.
     */
    @SuppressWarnings({"PMD.FieldNamingConventions", "PMD.LongVariable"})
    public enum CfrErrorType {
        FlightAlreadyExists,
        InternalError
    }
}
