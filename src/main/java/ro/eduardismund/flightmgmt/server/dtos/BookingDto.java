package ro.eduardismund.flightmgmt.server.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Represents a booking containing a passenger item, scheduled flight and assigned seat.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class BookingDto {

    @XmlElement
    private PassengerItem passenger;

    @XmlElement
    private ScheduledFlightItem scheduledFlight;

    @XmlElement
    private SeatItem assignedSeat;
}
