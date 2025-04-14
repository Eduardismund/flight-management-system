package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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

    @XmlAttribute
    private String flightId;

    @XmlAttribute
    private String departureDate;

    @XmlAttribute
    private int seatRow;

    @XmlAttribute
    private String seatName;
}
