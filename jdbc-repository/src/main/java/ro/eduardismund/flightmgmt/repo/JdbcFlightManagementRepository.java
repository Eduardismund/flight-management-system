package ro.eduardismund.flightmgmt.repo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.domain.Airplane;
import ro.eduardismund.flightmgmt.domain.Booking;
import ro.eduardismund.flightmgmt.domain.Flight;
import ro.eduardismund.flightmgmt.domain.Passenger;
import ro.eduardismund.flightmgmt.domain.ScheduledFlight;
import ro.eduardismund.flightmgmt.domain.Seat;
import ro.eduardismund.flightmgmt.domain.SeatingChart;

/**
 * The {@code JdbcFlightManagementRepository} class implements the {@link
 * JdbcFlightManagementRepository} interface and provides a relational database implementation of the flight
 * management repository, via JDBC. This class is responsible for managing flight-related data, including
 * flights, airplanes, scheduled flights, and bookings.
 */
@SuppressWarnings("PMD.TooManyMethods")
@SuppressFBWarnings("EI_EXPOSE_REP2")
@RequiredArgsConstructor
public class JdbcFlightManagementRepository implements FlightManagementRepository {

    static final String SELECT_SF =
            """
                    SELECT AirplaneIdNumber, Id, FlightNumber, ArrivalTime, DepartureTime
                    FROM ScheduledFlight
                    WHERE AirplaneIdNumber = ? AND DepartudeDate = ?""";
    static final String INSERT_SF =
            """
                    INSERT INTO ScheduledFlight(Id, AirplaneIdNumber, FlightNumber, DepartureTime,ArrivalTime)
                    VALUES(?,?,?,?,?)""";
    private final DataSource dataSource;

    @Override
    public void addFlight(Flight flight) {
        runInTransaction(conn -> {
            try (var insertFlight = conn.prepareStatement("INSERT INTO Flight(Number, Company) VALUES (?,?)")) {
                insertFlight.setString(1, flight.getNumber());
                insertFlight.setString(2, flight.getCompany());
                insertFlight.executeUpdate();
            }
            return null;
        });
    }

    @SneakyThrows
    Flight readFlightDetails(ResultSet resultSet) {
        final String number = resultSet.getString("Number");
        final String company = resultSet.getString("Company");

        final Flight flight = new Flight(number);
        flight.setCompany(company);
        return flight;
    }

    @SneakyThrows
    @Override
    public List<ScheduledFlight> findScheduledFlightsForAirplane(String airplaneIdNumber, LocalDate date) {
        final var result = new ArrayList<ScheduledFlight>();

        try (var conn = getConnection(true);
                var selectSF = conn.prepareStatement(SELECT_SF)) {
            selectSF.setString(1, airplaneIdNumber);
            selectSF.setDate(2, Date.valueOf(date));
            try (var resultSet = selectSF.executeQuery()) {
                if (resultSet.next()) {
                    result.add(readScheduledFlightDetails(conn, resultSet));
                }
            }
        }
        return result;
    }

    @SneakyThrows
    ScheduledFlight readScheduledFlightDetails(Connection conn, ResultSet resultSet) {
        final String airplaneIdNumber = resultSet.getString("AirplaneIdNumber");
        final String sfId = resultSet.getString("Id");
        final String flightNumber = resultSet.getString("FlightNumber");
        final LocalDateTime arrivalTime = resultSet.getTimestamp("ArrivalTime").toLocalDateTime();
        final LocalDateTime departureTime =
                resultSet.getTimestamp("DepartureTime").toLocalDateTime();

        final var scheduledFlight = new ScheduledFlight();
        scheduledFlight.setFlight(findFlight(conn, flightNumber).orElseThrow());
        scheduledFlight.setAirplane(findAirplane(conn, airplaneIdNumber).orElseThrow());
        scheduledFlight.setDepartureTime(departureTime);
        scheduledFlight.setArrivalTime(arrivalTime);

        scheduledFlight.setBookings(this.getBookingsOfScheduledFlight(sfId).stream()
                .peek(booking -> booking.setScheduledFlight(scheduledFlight))
                .collect(Collectors.toMap(Booking::getAssignedSeat, Function.identity())));

        return scheduledFlight;
    }

    @Override
    public void addScheduledFlight(ScheduledFlight scheduledFlight) {
        runInTransaction(conn -> {
            try (var insertSf = conn.prepareStatement(INSERT_SF)) {
                insertSf.setString(1, getScheduledFlightId(scheduledFlight));
                insertSf.setString(2, scheduledFlight.getAirplane().getIdNumber());
                insertSf.setString(3, scheduledFlight.getFlight().getNumber());
                insertSf.setTimestamp(4, Timestamp.valueOf(scheduledFlight.getDepartureTime()));
                insertSf.setTimestamp(5, Timestamp.valueOf(scheduledFlight.getArrivalTime()));

                insertSf.executeUpdate();

                for (final var booking : scheduledFlight.getBookings().values()) {
                    this.addBooking(booking);
                }
                return null;
            }
        });
    }

    @SneakyThrows
    @Override
    public Optional<ScheduledFlight> findScheduledFlight(String flightNumber, LocalDate localDate) {

        try (var conn = getConnection(true)) {
            return findScheduledFlightByWhereClause(
                    conn, "WHERE FlightNumber = ? AND CAST(DepartureTime AS DATE) = ?", selectSf -> {
                        selectSf.setString(1, flightNumber);
                        selectSf.setDate(2, Date.valueOf(localDate));
                    });
        }
    }

    @Override
    public void addBooking(Booking booking) {
        runInTransaction(conn -> {
            try (var insertBooking = conn.prepareStatement(
                    """
                            INSERT INTO SeatBooking(FirstName, LastName, IdDocument,
                            ScheduledFlightId, SeatRow, SeatName, BusinessClass)
                            VALUES(?,?,?,?,?,?,?)""")) {
                final var passenger = booking.getPassenger();
                insertBooking.setString(1, passenger.getFirstName());
                insertBooking.setString(2, passenger.getLastName());
                insertBooking.setString(3, passenger.getIdDocument());
                insertBooking.setString(4, getScheduledFlightId(booking.getScheduledFlight()));
                final var assignedSeat = booking.getAssignedSeat();
                insertBooking.setInt(5, assignedSeat.getRow());
                insertBooking.setString(6, assignedSeat.getSeatName());
                insertBooking.setBoolean(7, assignedSeat.isBusinessClass());

                insertBooking.executeUpdate();
            }
            return null;
        });
    }

    String getScheduledFlightId(ScheduledFlight scheduledFlight) {
        return scheduledFlight.getFlight().getNumber() + "_"
                + scheduledFlight.getDepartureTime().toLocalDate();
    }

    @Override
    public void addAirplane(Airplane airplane) {
        runInTransaction(conn -> {
            try (var insertAirplane =
                            conn.prepareStatement("INSERT INTO Airplane(IdNumber, Model,SeatsCount) VALUES(?,?,?)");
                    var insertSeat = conn.prepareStatement(
                            "INSERT INTO Seat(AirplaneIdNumber, Row,SeatName,BusinessClass) VALUES(?,?,?,?)")) {
                insertAirplane.setString(1, airplane.getIdNumber());
                insertAirplane.setString(2, airplane.getModel());
                insertAirplane.setInt(3, airplane.getSeatingChart().getSeatsCount());

                insertAirplane.executeUpdate();

                for (final var seat : airplane.getSeatingChart().getSeats()) {
                    insertSeat.setString(1, airplane.getIdNumber());
                    insertSeat.setInt(2, seat.getRow());
                    insertSeat.setString(3, seat.getSeatName());
                    insertSeat.setBoolean(4, seat.isBusinessClass());
                    insertSeat.executeUpdate();
                }
            }
            return null;
        });
    }

    @SuppressWarnings({"UnusedReturnValue", "PMD.AvoidCatchingGenericException"})
    @SneakyThrows
    <T> T runInTransaction(ConnCallable<T> callable) {
        try (var conn = getConnection(false)) {
            try {
                final var res = callable.call(conn);
                conn.commit();
                return res;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    Airplane readAirplaneDetails(Connection conn, ResultSet resultSet) throws SQLException {
        final String idNumber = resultSet.getString("IdNumber");
        final String model = resultSet.getString("Model");
        final int seatsCount = resultSet.getInt("SeatsCount");

        final Set<Seat> seats = getSeatingChart(conn, idNumber);
        final var seatingChart = new SeatingChart(seatsCount, seats);
        final Airplane airplane = new Airplane(String.valueOf(idNumber));
        airplane.setModel(model);
        airplane.setSeatingChart(seatingChart);
        return airplane;
    }

    @SneakyThrows
    @Override
    public boolean contains(Airplane airplane) {
        boolean found = false;
        try (var conn = getConnection(true);
                var selectAirplanes =
                        conn.prepareStatement("Select count(IdNumber) From Airplane WHERE IdNumber = ?")) {
            selectAirplanes.setString(1, airplane.getIdNumber());
            try (var resultSet = selectAirplanes.executeQuery()) {
                if (resultSet.next()) {
                    found = resultSet.getInt(1) == 1;
                }
            }
        }
        return found;
    }

    @SneakyThrows
    @Override
    public boolean contains(Flight flight) {
        boolean found = false;
        try (var conn = getConnection(true);
                var selectFlights = conn.prepareStatement("Select count(Number) From Flight WHERE Number = ?")) {
            selectFlights.setString(1, flight.getNumber());
            try (var resultSet = selectFlights.executeQuery()) {
                if (resultSet.next()) {
                    found = resultSet.getInt(1) == 1;
                }
            }
        }
        return found;
    }

    @SneakyThrows
    @Override
    public List<Flight> getFlights() {
        final List<Flight> flights = new ArrayList<>();
        try (var conn = getConnection(true);
                var selectFlights = conn.prepareStatement("SELECT Number, Company FROM Flight");
                var resultSet = selectFlights.executeQuery()) {
            while (resultSet.next()) {
                flights.add(readFlightDetails(resultSet));
            }
        }
        return flights;
    }

    @SneakyThrows
    @Override
    public List<Airplane> getAirplanes() {
        final List<Airplane> airplanes = new ArrayList<>();
        try (var conn = getConnection(true);
                var selectAirplanes = conn.prepareStatement("SELECT IdNumber, Model, SeatsCount FROM Airplane");
                var resultSet = selectAirplanes.executeQuery()) {
            while (resultSet.next()) {
                airplanes.add(readAirplaneDetails(conn, resultSet));
            }
        }
        return airplanes;
    }

    Set<Seat> getSeatingChart(Connection conn, String idNumber) throws SQLException {
        try (var selectSeats = conn.prepareStatement(
                """
                        SELECT s.Row, s.SeatName, s.BusinessClass
                        FROM Seat s
                        WHERE s.AirplaneIdNumber = ?""")) {

            selectSeats.setString(1, idNumber);
            try (var resultSet2 = selectSeats.executeQuery()) {

                final Set<Seat> seats = new HashSet<>();

                while (resultSet2.next()) {
                    final int row = resultSet2.getInt("Row");
                    final String seatsPerRow = resultSet2.getString("SeatName");
                    final boolean businessClass = resultSet2.getBoolean("BusinessClass");
                    seats.add(new Seat(row, seatsPerRow, businessClass));
                }
                return seats;
            }
        }
    }

    @SneakyThrows
    @Override
    public List<ScheduledFlight> getScheduledFlights() {
        final List<ScheduledFlight> scheduledFlights = new ArrayList<>();
        try (var conn = getConnection(true);
                var selectSf = conn.prepareStatement(
                        "SELECT AirplaneIdNumber, Id, FlightNumber, DepartureTime,ArrivalTime FROM ScheduledFlight");
                var resultSet = selectSf.executeQuery()) {
            while (resultSet.next()) {
                scheduledFlights.add(readScheduledFlightDetails(conn, resultSet));
            }
        }
        return scheduledFlights;
    }

    @SneakyThrows
    Optional<Airplane> findAirplane(Connection conn, String airplaneId) {
        var result = Optional.<Airplane>empty();
        try (var selectAirplanes = conn.prepareStatement(
                """
                        SELECT IdNumber, Model, SeatsCount
                        FROM Airplane WHERE IdNumber = ?""")) {
            selectAirplanes.setString(1, airplaneId);
            try (var resultSet = selectAirplanes.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(readAirplaneDetails(conn, resultSet));
                }
            }
        }
        return result;
    }

    @SneakyThrows
    @Override
    public Optional<Airplane> findAirplane(String airplaneNumber) {
        try (var conn = getConnection(true)) {
            return findAirplane(conn, airplaneNumber);
        }
    }

    @SneakyThrows
    Optional<Flight> findFlight(Connection conn, String flightNumber) {
        var result = Optional.<Flight>empty();
        try (var selectFlights = conn.prepareStatement("SELECT Number, Company  FROM Flight WHERE Number = ?")) {
            selectFlights.setString(1, flightNumber);
            try (var resultSet = selectFlights.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(readFlightDetails(resultSet));
                }
            }
        }
        return result;
    }

    @SneakyThrows
    @Override
    public Optional<Flight> findFlight(String flightNumber) {
        try (var conn = getConnection(true)) {
            return findFlight(conn, flightNumber);
        }
    }

    @SneakyThrows
    List<Booking> getBookingsOfScheduledFlight(String scheduledFlightId) {
        final List<Booking> bookings = new ArrayList<>();
        try (var conn = getConnection(true);
                var selectBookings = conn.prepareStatement(
                        """
                             SELECT FirstName, LastName, IdDocument, ScheduledFlightId, SeatRow, SeatName, BusinessClass
                             FROM SeatBooking
                             WHERE ScheduledFlightId = ?""")) {
            selectBookings.setString(1, scheduledFlightId);
            try (var resultSet = selectBookings.executeQuery()) {
                while (resultSet.next()) {
                    final Booking booking = readBookingDetails(resultSet);
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    @SneakyThrows
    Booking readBookingDetails(ResultSet resultSet) {
        final String firstName = resultSet.getString("FirstName");
        final String lastName = resultSet.getString("LastName");
        final String idDocument = resultSet.getString("IdDocument");
        final int row = resultSet.getInt("SeatRow");
        final String seatName = resultSet.getString("SeatName");
        final boolean businessClass = resultSet.getBoolean("BusinessClass");
        final Booking booking = new Booking();
        booking.setAssignedSeat(new Seat(row, seatName, businessClass));
        booking.setPassenger(new Passenger(firstName, lastName, idDocument));
        return booking;
    }

    @SneakyThrows
    Optional<ScheduledFlight> findScheduledFlightByWhereClause(
            Connection conn, String whereClause, PsParamsSetter psParamsSetter) {
        var result = Optional.<ScheduledFlight>empty();
        try (var selectSf = conn.prepareStatement(
                """
                        SELECT AirplaneIdNumber, FlightNumber, ArrivalTime, DepartureTime
                        FROM ScheduledFlight
                        """
                        + whereClause); ) {
            psParamsSetter.setParams(selectSf);
            try (var resultSet = selectSf.executeQuery()) {
                if (resultSet.next()) {
                    result = Optional.of(readScheduledFlightDetails(conn, resultSet));
                }
            }
        }
        return result;
    }

    Connection getConnection(boolean readOnly) throws SQLException {
        final var conn = dataSource.getConnection();
        conn.setAutoCommit(false);
        conn.setReadOnly(readOnly);
        return conn;
    }

    interface ConnCallable<T> {

        @SuppressWarnings("PMD.SignatureDeclareThrowsException")
        T call(Connection conn) throws Exception;
    }

    interface PsParamsSetter {
        void setParams(PreparedStatement preparedStatement) throws SQLException;
    }
}
