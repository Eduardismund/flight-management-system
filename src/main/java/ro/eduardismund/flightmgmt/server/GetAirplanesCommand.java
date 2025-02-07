package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Command for requesting airplane data.
 * Used for XML binding via JAXB.
 */
@XmlRootElement(name = "getAirplanesCommand")
public class GetAirplanesCommand {}
