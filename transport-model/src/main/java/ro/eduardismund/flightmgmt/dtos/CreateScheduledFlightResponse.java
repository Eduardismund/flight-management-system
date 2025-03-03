package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Response for creating a scheduled flight.
 */
@XmlRootElement(name = "createScheduledFlightResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateScheduledFlightResponse {
    @XmlElement
    private String flightId;

    @XmlElement
    private String airplaneId;

    @XmlElement
    private boolean success;

    @XmlElement
    private String departureTime;

    @XmlElement
    private String arrivalTime;

    @XmlElement
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
