package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import ro.eduardismund.flightmgmt.server.dtos.PassengerItem;
import ro.eduardismund.flightmgmt.server.dtos.ScheduledFlightItem;
import ro.eduardismund.flightmgmt.server.dtos.SeatItem;

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
