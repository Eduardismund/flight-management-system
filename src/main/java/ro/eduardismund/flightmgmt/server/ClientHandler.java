package ro.eduardismund.flightmgmt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

@SuppressWarnings("PMD.DoNotUseThreads")
@RequiredArgsConstructor
class ClientHandler extends Thread {
    final FlightManagementService service;
    final DomainMapper domainMapper;
    final Supplier<Socket> clientSocketSupplier;
    final XmlManager xmlManager;
    final CliManager cliManager;

    @SuppressWarnings("rawtypes")
    final Map<Class<?>, CommandHandler> handlerMap = Map.of(
            CreateAirplaneCommand.class, new CreateAirplaneCommandHandler(),
            CreateBookingCommand.class, new CreateBookingCommandHandler(),
            CreateFlightCommand.class, new CreateFlightCommandHandler(),
            CreateScheduledFlightCommand.class, new CreateScheduledFlightCommandHandler(),
            GetFlightsCommand.class, new GetFlightsCommandHandler(),
            GetScheduledFlightsCommand.class, new GetScheduledFlightsCommandHandler(),
            GetAirplanesCommand.class, new GetAirplanesCommandHandler(),
            FindFlightCommand.class, new FindFlightCommandHandler(),
            FindAirplaneCommand.class, new FindAirplaneCommandHandler(),
            FindScheduledFlightCommand.class, new FindScheduledFlightCommandHandler());

    @SuppressWarnings({"PMD.UseTryWithResources", "PMD.AssignmentInOperand"})
    @Override
    public void run() {
        try (var clientSocket = clientSocketSupplier.get();
                var reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                var writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            String commandLine;
            while ((commandLine = reader.readLine()) != null) {
                handleCommand(commandLine, writer);
            }
        } catch (IOException e) {
            cliManager.printException(e);
        }
    }

    private void handleCommand(String commandLine, PrintWriter writer) {
        final var command = xmlManager.unmarshal(new StringReader(commandLine));
        final var handler = findHandler(command);
        if (handler == null) {
            cliManager.println("Unknown command: " + command);
            return;
        }
        @SuppressWarnings("unchecked")
        final var response = handler.handleCommand(command, service, domainMapper);

        xmlManager.marshal(response, writer);
        writer.println();
    }

    @SuppressWarnings("rawtypes")
    CommandHandler findHandler(Object command) {
        return handlerMap.get(command.getClass());
    }
}
