package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Override
    public int compareTo(SeatItem o) {
        final var compare = Integer.compare(row, o.row);
        if (compare != 0) {
            return compare;
        }
        return seatName.compareTo(o.seatName);
    }
}
