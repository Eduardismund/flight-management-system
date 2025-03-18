package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for creating a scheduled flight.
 */
@XmlRootElement(name = "createScheduledFlightResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateScheduledFlightResponse {
    @XmlAttribute
    private String flightId;

    @XmlAttribute
    private String airplaneId;

    @XmlAttribute
    private boolean success;

    @XmlAttribute
    private String departureTime;

    @XmlAttribute
    private String arrivalTime;

    @XmlAttribute
    private CsfrErrorType error;

    /**
     * Enum to represent error types during scheduled flight creation.
     */
    @SuppressWarnings({"PMD.FieldNamingConventions", "PMD.LongVariable"})
    public enum CsfrErrorType {
        ScheduledFlightAlreadyExistsException,
        AirplaneAlreadyScheduledException,
        ArrivalBeforeDepartureException,
        InternalError
    }
}
