package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Command for requesting flight data.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getFlightsCommand")
public class GetFlightsCommand {}
