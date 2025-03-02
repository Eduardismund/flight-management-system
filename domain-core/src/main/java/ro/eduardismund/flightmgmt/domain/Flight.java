package ro.eduardismund.flightmgmt.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * The {@code Flight} class represents a flight entity in the flight management system. This class
 * implement {@link Serializable} to allow the flight's state to be persisted and retrieved from
 * storage. It contains information about the flight's unique number and its company.
 */
@RequiredArgsConstructor
@Data
public class Flight implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String number;
    private String company;
}
