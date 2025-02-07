package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.*;
import java.util.Set;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SeatingChartDto {
    @XmlElement
    private Set<SeatItem> seats = new TreeSet<>();
}
