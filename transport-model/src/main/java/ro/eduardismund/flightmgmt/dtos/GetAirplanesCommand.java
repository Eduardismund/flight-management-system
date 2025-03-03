package ro.eduardismund.flightmgmt.dtos;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Command for requesting airplane data.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getAirplanesCommand")
public class GetAirplanesCommand {}
