package ro.eduardismund.flightmgmt.domain;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * The {@code Airplane} class represents an airplane entity in the flight management system. It
 * implements {@link Serializable} to allow the airplane's state to be persisted and retrieved from
 * storage. Each airplane has a unique ID number, a model name and an associated seating chart
 */
@Data
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Airplane implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String idNumber;
    private String model;
    private SeatingChart seatingChart;
}
