package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.Marshaller;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

@RequiredArgsConstructor
class ClientHandler extends Thread {
    private final FlightManagementService service;
    private final DomainMapper domainMapper;
    private final Socket clientSocket;

    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, CommandHandler> handlerMap = Map.of(
            CreateAirplaneCommand.class, new CreateAirplaneCommandHandler(),
            CreateFlightCommand.class, new CreateFlightCommandHandler(),
            GetFlightsCommand.class, new GetFlightsCommandHandler(),
            GetAirplanesCommand.class, new GetAirplanesCommandHandler());

    @SneakyThrows
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            final var unmar = JaxbUtil.createUnmarshaller();
            Marshaller mar = JaxbUtil.createMarshaller();

            String commandLine;
            while ((commandLine = in.readLine()) != null) {
                final var command = unmar.unmarshal(new StringReader(commandLine));
                final var handler = handlerMap.get(command.getClass());
                if (handler == null) {
                    System.err.println("Unknown command: " + command);
                    continue;
                }
                @SuppressWarnings("unchecked")
                final var response = handler.handleCommand(command, service, domainMapper);

                mar.marshal(response, out);
                out.println();
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
