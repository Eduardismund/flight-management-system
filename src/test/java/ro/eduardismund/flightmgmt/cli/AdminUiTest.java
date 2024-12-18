package ro.eduardismund.flightmgmt.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.service.AirplaneAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.FlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.ScheduledFlightAlreadyExistsException;
import ro.eduardismund.flightmgmt.service.Service;

@ExtendWith(MockitoExtension.class)
class AdminUiTest {
    public static final String FLIGHT_NUMBER = "F123";
    public static final String AIRPLANE_NUMBER = "A123";

    @Mock
    private Service service;

    @Mock
    private CliManager cliManager;

    @InjectMocks
    private AdminUi adminUi;

    @SneakyThrows
    @Test
    void createBooking_flightDoesntExist() {

        when(cliManager.println(anyString())).thenReturn(cliManager);
        when(cliManager.readLine()).thenReturn(FLIGHT_NUMBER);
        when(cliManager.readDate(anyString())).thenReturn(LocalDate.of(2024, 12, 12));

        when(service.findScheduledFlight(anyString(), any())).thenReturn(Optional.empty());

        adminUi.createBooking();

        verify(cliManager).println("Flight F123 not found on 2024-12-12");
    }

    @SneakyThrows
    @Test
    void createBooking_flightExists() {

        final var sf = new ScheduledFlight();
        sf.setFlight(new Flight(FLIGHT_NUMBER));

        sf.setDepartureTime(LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(12, 12, 12)));
        sf.setArrivalTime(LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(13, 13, 13)));
        when(cliManager.println(anyString())).thenReturn(cliManager);
        when(cliManager.readLine())
                .thenReturn(FLIGHT_NUMBER)
                .thenReturn("Eduard")
                .thenReturn("Jitareanu")
                .thenReturn("1234")
                .thenReturn("B");
        when(cliManager.readInt()).thenReturn(2);

        when(cliManager.readDate(anyString())).thenReturn(LocalDate.of(2024, 12, 12));

        when(service.findScheduledFlight(anyString(), any())).thenReturn(Optional.of(sf));

        final var newBookingCaptor = ArgumentCaptor.<Booking>captor();

        doNothing().when(service).createBooking(newBookingCaptor.capture());

        adminUi.createBooking();

        verify(cliManager)
                .println("Flight F123 is departing at: 2024-12-12T12:12:12 and arriving at: 2024-12-12T13:13:13");

        assertEquals(
                FLIGHT_NUMBER,
                newBookingCaptor.getValue().getScheduledFlight().getFlight().getNumber());

        assertEquals("Eduard", newBookingCaptor.getValue().getPassenger().getFirstName());
        assertEquals("Jitareanu", newBookingCaptor.getValue().getPassenger().getLastName());
        assertEquals("1234", newBookingCaptor.getValue().getPassenger().getIdDocument());

        assertEquals(2, newBookingCaptor.getValue().getAssignedSeat().getRow());
        assertEquals("B", newBookingCaptor.getValue().getAssignedSeat().getSeatName());

        assertEquals(
                LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(12, 12, 12)),
                newBookingCaptor.getValue().getScheduledFlight().getDepartureTime());
        assertEquals(
                LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(13, 13, 13)),
                newBookingCaptor.getValue().getScheduledFlight().getArrivalTime());
        verify(cliManager).println("Booking created!");
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"true", "false"})
    void createFlight(boolean isAlready) {
        final var flights = List.of(new Flight("F124"));
        when(cliManager.printAll(any(), anyString(), anyString())).thenReturn(cliManager);

        when(cliManager.println(anyString())).thenReturn(cliManager).thenReturn(cliManager);
        when(cliManager.readLine()).thenReturn(FLIGHT_NUMBER).thenReturn("Tarom");

        when(service.getFlights()).thenReturn(flights);

        final var newFlightCaptor = ArgumentCaptor.<Flight>captor();

        if (isAlready) {
            doThrow(new FlightAlreadyExistsException(FLIGHT_NUMBER))
                    .when(service)
                    .createFlight(any(Flight.class));
        } else {
            doNothing().when(service).createFlight(newFlightCaptor.capture());
        }

        adminUi.createFlight();

        verify(service).getFlights();
        verify(cliManager).printAll(flights, "The list of Flights: ", "No Flights yet! Ooupsie!");

        if (isAlready) {
            verify(cliManager).println("Flight already exists!");
        } else {
            assertEquals(FLIGHT_NUMBER, newFlightCaptor.getValue().getNumber());
            assertEquals("Tarom", newFlightCaptor.getValue().getCompany());
            verify(cliManager).println("Flight created");
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"true", "false"})
    void createAirplane(boolean isAlready) {
        final var airplanes = List.of(new Airplane(AIRPLANE_NUMBER));
        when(cliManager.printAll(any(), anyString(), anyString())).thenReturn(cliManager);

        when(cliManager.println(anyString()))
                .thenReturn(cliManager)
                .thenReturn(cliManager)
                .thenReturn(cliManager)
                .thenReturn(cliManager);
        when(cliManager.readLine()).thenReturn(AIRPLANE_NUMBER).thenReturn("Cargo");
        when(cliManager.readInt()).thenReturn(2).thenReturn(2);

        when(service.getAirplanes()).thenReturn(airplanes);

        final var newAirplaneCaptor = ArgumentCaptor.<Airplane>captor();

        if (isAlready) {
            doThrow(new AirplaneAlreadyExistsException(AIRPLANE_NUMBER))
                    .when(service)
                    .createAirplane(any(Airplane.class));
        } else {
            doNothing().when(service).createAirplane(newAirplaneCaptor.capture());
        }

        adminUi.createAirplane();

        verify(service).getAirplanes();
        verify(cliManager).printAll(airplanes, "The list of Airplanes: ", "No Airplanes yet!");

        if (isAlready) {
            verify(cliManager).println("Airplane already exists!");
        } else {
            assertEquals(AIRPLANE_NUMBER, newAirplaneCaptor.getValue().getIdNumber());
            assertEquals("Cargo", newAirplaneCaptor.getValue().getModel());
            assertEquals(4, newAirplaneCaptor.getValue().getSeatingChart().getSeatsCount());
            verify(cliManager).println("Airplane created");
        }
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"true", "false"})
    void createScheduledFlight(boolean isAlready) {
        final var sf = List.of(new ScheduledFlight());
        final var flight = new Flight(FLIGHT_NUMBER);

        final var airplane = new Airplane(AIRPLANE_NUMBER);

        when(cliManager.printAll(any(), anyString(), anyString())).thenReturn(cliManager);

        when(cliManager.readById(anyString(), any())).thenReturn(flight).thenReturn(airplane);
        when(cliManager.readDateTime(anyString()))
                .thenReturn(LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(12, 12, 12)))
                .thenReturn(LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(13, 13, 13)));

        when(service.getAirplanes()).thenReturn(List.of(airplane));
        when(service.getFlights()).thenReturn(List.of(flight));
        when(service.getScheduledFlights()).thenReturn(sf);

        final var newSfCaptor = ArgumentCaptor.<ScheduledFlight>captor();

        if (isAlready) {
            doThrow(new ScheduledFlightAlreadyExistsException(AIRPLANE_NUMBER, LocalDate.of(2024, 12, 12)))
                    .when(service)
                    .createScheduledFlight(any(ScheduledFlight.class));
        } else {
            doNothing().when(service).createScheduledFlight(newSfCaptor.capture());
        }

        adminUi.createScheduledFlight();

        verify(service).getAirplanes();
        verify(service).getFlights();
        verify(service).getScheduledFlights();
        verify(cliManager).printAll(List.of(flight), "The list of Flights: ", "No Flights yet! Ooupsie!");
        verify(cliManager).printAll(List.of(airplane), "The list of Airplanes: ", "No Airplanes yet!");
        verify(cliManager).printAll(sf, "The list of Scheduled Flights: ", "No Scheduled Flights yet!");

        if (isAlready) {
            verify(cliManager).println("A flight with number A123 already exists on 2024-12-12");
        } else {
            assertEquals(FLIGHT_NUMBER, newSfCaptor.getValue().getFlight().getNumber());
            assertEquals(AIRPLANE_NUMBER, newSfCaptor.getValue().getAirplane().getIdNumber());
            assertEquals(
                    LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(12, 12, 12)),
                    newSfCaptor.getValue().getDepartureTime());
            assertEquals(
                    LocalDateTime.of(LocalDate.of(2024, 12, 12), LocalTime.of(13, 13, 13)),
                    newSfCaptor.getValue().getArrivalTime());
            verify(cliManager).println("Scheduled Flight created!");
        }
    }
}
