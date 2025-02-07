package ro.eduardismund.flightmgmt.server;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ServerConfigProperties {

    @Builder.Default
    int port = 5000;
}
