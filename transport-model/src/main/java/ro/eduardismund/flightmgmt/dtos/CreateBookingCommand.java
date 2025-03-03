package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * Command to create a booking.
 */
@XmlRootElement(name = "createBookingCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateBookingCommand {
    @XmlElement
    private PassengerItem passenger;

    @XmlElement
    private ScheduledFlightItem scheduledFlight;

    @XmlElement
    private SeatItem assignedSeat;
}
