package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a seat on an airplane with row, seat name, and business class status.
 * Implements {@link Comparable} for sorting by row and seat name.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatItem implements Comparable<SeatItem> {

    @XmlAttribute
    private int row;

    @XmlAttribute
    private String seatName;

    @XmlAttribute
    private boolean businessClass;

    /**
     * Compares this seat with another by row and seat name.
     */
    @Override
    public int compareTo(SeatItem seatItem) {
        final var compare = Integer.compare(row, seatItem.row);
        if (compare != 0) {
            return compare;
        }
        return seatName.compareTo(seatItem.seatName);
    }
}
