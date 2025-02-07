package ro.eduardismund.flightmgmt.server;

import lombok.Builder;
import lombok.Value;

/**
 * Configuration properties for the server, including host and port.
 * Default host is "localhost", and default port is 5000.
 */
@Value
@Builder
public class ServerConfigProperties {

    /**
     * The host where the server runs. Default is "localhost".
     */
    @Builder.Default
    String host = "localhost";

    /**
     * The port the server listens on. Default is 5000.
     */
    @Builder.Default
    int port = 5000;
}
