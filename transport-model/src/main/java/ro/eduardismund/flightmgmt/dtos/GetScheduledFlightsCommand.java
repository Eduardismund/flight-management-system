package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Command to request scheduled flights.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getScheduledFlightCommand")
public class GetScheduledFlightsCommand {}
