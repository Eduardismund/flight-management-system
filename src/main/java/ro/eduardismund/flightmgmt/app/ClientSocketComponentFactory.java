package ro.eduardismund.flightmgmt.app;

import java.net.Socket;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.server.ServerConfigProperties;

public class ClientSocketComponentFactory implements ComponentFactory<Socket> {
    @SneakyThrows
    @Override
    public Socket createComponent(Environment properties, ComponentResolver resolver) {

        final var config = resolver.resolveComponent(ServerConfigProperties.class);
        return new Socket("localhost", config.getPort());
    }
}
