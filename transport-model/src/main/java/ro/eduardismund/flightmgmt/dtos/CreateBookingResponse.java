package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for creating a flight.
 */
@XmlRootElement(name = "createBookingResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateBookingResponse {
    @XmlAttribute
    private boolean success;

    @XmlAttribute
    private CreateBookingResponse.CbrErrorType error;

    /**
     * Enum to represent error types during airplane creation.
     */
    @SuppressWarnings({"PMD.FieldNamingConventions", "PMD.LongVariable"})
    public enum CbrErrorType {
        InternalError
    }
}
