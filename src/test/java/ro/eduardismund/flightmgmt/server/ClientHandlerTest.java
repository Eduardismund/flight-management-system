package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.eduardismund.flightmgmt.cli.CliManager;
import ro.eduardismund.flightmgmt.service.DomainMapper;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

@SuppressWarnings("CallToThreadRun")
class ClientHandlerTest {
    private static final String COMMAND = "FlightCommand\nCAACAT\n";
    private FlightManagementService service;
    private DomainMapper domainMapper;
    private Socket clientSocket;
    private ClientHandler subject;
    private XmlManager xmlManager;
    private CliManager cliManager;

    @SuppressWarnings("rawtypes")
    private CommandHandler handler;

    @BeforeEach
    void setUp() {
        service = mock(FlightManagementService.class);
        domainMapper = mock(DomainMapper.class);
        clientSocket = mock(Socket.class);
        handler = mock(CommandHandler.class);
        xmlManager = mock(XmlManager.class);
        cliManager = mock(CliManager.class);
        subject = spy(new ClientHandler(service, domainMapper, clientSocket, xmlManager, cliManager));
    }

    @Test
    void run_isSuccessful() throws IOException {
        final var inputStream = new ByteArrayInputStream(COMMAND.getBytes());
        final var outputStream = new ByteArrayOutputStream();

        doReturn(inputStream).when(clientSocket).getInputStream();
        doReturn(outputStream).when(clientSocket).getOutputStream();
        doAnswer(inv -> new Cmd(readString(inv.getArgument(0, Reader.class))))
                .when(xmlManager)
                .unmarshal(any());

        doReturn(handler).when(subject).findHandler(any(Cmd.class));
        //noinspection unchecked
        doAnswer(inv -> new Res(inv.getArgument(0, Cmd.class).str()))
                .when(handler)
                .handleCommand(any(Cmd.class), same(service), same(domainMapper));
        doAnswer(invocation -> {
                    invocation
                            .getArgument(1, Writer.class)
                            .write(invocation.getArgument(0, Res.class).toString());
                    return null;
                })
                .when(xmlManager)
                .marshal(any(Res.class), any());

        subject.run();

        assertEquals(
                """
                Res[str=FlightCommand]
                Res[str=CAACAT]
                """,
                outputStream.toString().replaceAll("\r", ""));

        assertClientSocketClosed();
    }

    @Test
    void run_handlerIsNull() throws IOException {
        final var inputStream = new ByteArrayInputStream(COMMAND.getBytes());
        final var outputStream = new ByteArrayOutputStream();
        final var command = new CreateFlightCommand();

        doReturn(inputStream).when(clientSocket).getInputStream();
        doReturn(outputStream).when(clientSocket).getOutputStream();

        doReturn(command).when(xmlManager).unmarshal(any());
        doReturn(null).when(subject).findHandler(command);

        subject.run();

        assertClientSocketClosed();
        verify(cliManager, times(2)).println("Unknown command: " + command);
    }

    private void assertClientSocketClosed() throws IOException {
        verify(clientSocket).close();
        verify(cliManager).println("Client disconnected.");
    }

    @Test
    void run_catchesIOException() throws IOException {
        final var ioException = new IOException();
        doThrow(ioException).when(clientSocket).getInputStream();

        subject.run();

        verify(cliManager).printException(same(ioException));
        assertClientSocketClosed();
    }

    @Test
    void run_catchesIOException_onClientSocketClose() throws IOException {
        final var ioException = new IOException();
        doThrow(new IOException()).when(clientSocket).getInputStream();
        doThrow(ioException).when(clientSocket).close();

        subject.run();

        verify(cliManager).printException(same(ioException));
    }

    private String readString(Reader reader) {
        return new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
    }

    private record Cmd(String str) {}

    private record Res(String str) {}

    @Test
    void findHandler_returns_handlersMap_get() {
        verifyHandlerMapContent();
    }

    private void verifyHandlerMapContent() {
        subject.handlerMap.forEach(this::assertFindHandler);
    }

    @SuppressWarnings("rawtypes")
    @SneakyThrows
    private void assertFindHandler(Class<?> key, CommandHandler value) {
        final var constructor = Arrays.stream(key.getConstructors())
                .filter(constr -> constr.getParameterCount() == 0)
                .findFirst()
                .orElseThrow();
        final var instance = constructor.newInstance();
        assertSame(value, subject.findHandler(instance));
    }

    @Test
    void handlerMapKeys_correspondToXmlManagerClasses() {
        assertEquals(
                subject.handlerMap.keySet(),
                Arrays.stream(XmlManager.CLASSES)
                        .filter(cls -> cls.getName().endsWith("Command"))
                        .collect(Collectors.toSet()));
    }
}
