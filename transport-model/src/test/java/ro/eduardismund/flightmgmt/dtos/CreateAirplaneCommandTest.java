package ro.eduardismund.flightmgmt.dtos;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class CreateAirplaneCommandTest {

    @SuppressWarnings("checkstyle:LineLength")
    public static final String COMMAND =
            """
                    <createAirplaneCommand idNumber="1" model="a"><seatingChart><seats><seat row="1" seatName="A" businessClass="true"/><seat row="1" seatName="B" businessClass="true"/><seat row="2" seatName="C" businessClass="false"/></seats></seatingChart></createAirplaneCommand>""";

    @SneakyThrows
    @Test
    void testMarshal() {
        final var airplaneCommand = new CreateAirplaneCommand();
        airplaneCommand.setModel("a");
        airplaneCommand.setIdNumber("1");
        SeatingChartDto seatingChart = new SeatingChartDto();
        seatingChart.getSeats().add(new SeatItem(1, "A", true));
        seatingChart.getSeats().add(new SeatItem(1, "B", true));
        seatingChart.getSeats().add(new SeatItem(2, "C", false));

        airplaneCommand.setSeatingChart(seatingChart);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final var out = new PrintWriter(outputStream, true);
        JAXBContext context = JAXBContext.newInstance(CreateAirplaneCommand.class);

        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        mar.marshal(airplaneCommand, out);

        final var xml = outputStream.toString();
        assertEquals(COMMAND, xml);
    }
}
