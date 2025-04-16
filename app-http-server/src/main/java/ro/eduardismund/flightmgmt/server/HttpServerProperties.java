package ro.eduardismund.flightmgmt.server;

import lombok.Builder;

/**
 * Http Server Properties.
 *
 * @param port port to run the server on
 */
@Builder
public record HttpServerProperties(int port) {}
