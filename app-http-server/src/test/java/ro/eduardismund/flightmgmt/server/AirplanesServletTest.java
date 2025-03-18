package ro.eduardismund.flightmgmt.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.dtos.*;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightManagementService;

class AirplanesServletTest {
    public static final String AIRPLANE_NUMBER = "A123";
    private XmlManager xmlManager;
    private FlightManagementService service;
    private AirplanesServlet subject;
    private DomainMapper domainMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        xmlManager = mock(XmlManager.class);
        domainMapper = mock(DomainMapper.class);
        service = mock(FlightManagementService.class);

        subject = new AirplanesServlet(xmlManager, service, domainMapper);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void doGet_findAirplane(boolean isPresent) throws IOException {
        final var captor = ArgumentCaptor.forClass(FindAirplaneCommandResponse.class);
        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        doReturn("/airplanes/" + AIRPLANE_NUMBER).when(request).getRequestURI();

        if (isPresent) {
            final var airplane = new Airplane(AIRPLANE_NUMBER);
            final var airplaneItem = new AirplaneItem();
            airplaneItem.setIdNumber(AIRPLANE_NUMBER);
            doReturn(airplaneItem).when(domainMapper).mapToAirplaneItem(airplane);
            doReturn(Optional.of(airplane)).when(service).findAirplane(AIRPLANE_NUMBER);
        } else {
            doReturn(Optional.empty()).when(service).findAirplane(AIRPLANE_NUMBER);
        }
        subject.doGet(request, response);

        if (isPresent) {
            assertEquals(AIRPLANE_NUMBER, captor.getValue().getAirplaneItem().getIdNumber());
            assertTrue(captor.getValue().isFound());
        } else {
            verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
            assertNull(captor.getValue().getAirplaneItem());
            assertFalse(captor.getValue().isFound());
        }
        verify(response).setContentType("text/xml");
    }

    @Test
    void doGet_AirplanesList() throws IOException {
        final var airplane = new Airplane(AIRPLANE_NUMBER);
        final var airplanes = List.of(airplane);
        final var captor = ArgumentCaptor.forClass(GetAirplanesResponse.class);
        final var airplaneItem = new AirplaneItem();
        airplaneItem.setIdNumber(AIRPLANE_NUMBER);

        final var writer = mock(PrintWriter.class);
        doReturn(writer).when(response).getWriter();
        doReturn("/airplanes").when(request).getRequestURI();
        doReturn(airplanes).when(service).getAirplanes();
        doReturn(airplaneItem).when(domainMapper).mapToAirplaneItem(airplane);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));

        subject.doGet(request, response);

        assertSame(airplaneItem, captor.getValue().getAirplanes().getFirst());
        assertTrue(captor.getValue().isSuccess());
    }

    @ParameterizedTest
    @CsvSource({"success", "AirplaneAlreadyExistsException", "InternalError"})
    void doPost(String type) throws IOException, AirplaneAlreadyExistsException {
        final var command = mock(CreateAirplaneCommand.class);
        final var airplane = new Airplane(AIRPLANE_NUMBER);
        final var captor = ArgumentCaptor.forClass(CreateAirplaneResponse.class);
        final var reader = mock(BufferedReader.class);
        final var writer = mock(PrintWriter.class);
        doReturn(reader).when(request).getReader();
        doReturn(writer).when(response).getWriter();

        doReturn(command).when(xmlManager).unmarshal(reader);
        doReturn(AIRPLANE_NUMBER).when(command).getIdNumber();
        doReturn(airplane).when(domainMapper).mapFromCreateAirplaneCommand(command);
        doNothing().when(xmlManager).marshal(captor.capture(), same(writer));
        switch (type) {
            case "success" -> doNothing().when(service).createAirplane(airplane);
            case "AirplaneAlreadyExistsException" -> doThrow(AirplaneAlreadyExistsException.class)
                    .when(service)
                    .createAirplane(airplane);
            case "InternalError" -> doThrow(RuntimeException.class)
                    .when(service)
                    .createAirplane(airplane);
        }
        subject.doPost(request, response);

        switch (type) {
            case "success" -> {
                assertTrue(captor.getValue().isSuccess());
                verify(response).setStatus(HttpServletResponse.SC_CREATED);
            }
            case "AirplaneAlreadyExistsException" -> {
                verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
                assertEquals(
                        CreateAirplaneResponse.CarErrorType.AirplaneAlreadyExists,
                        captor.getValue().getError());
            }
            case "InternalError" -> {
                verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                assertEquals(
                        CreateAirplaneResponse.CarErrorType.InternalError,
                        captor.getValue().getError());
            }
        }
        verify(response).setContentType("text/xml");
        assertEquals(airplane.getIdNumber(), captor.getValue().getAirplaneId());
    }
}
