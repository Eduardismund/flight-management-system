package ro.eduardismund.flightmgmt.server;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "createAirplaneResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CreateAirplaneResponse {
    @XmlAttribute
    private String airplaneId;

    @XmlElement
    private boolean success;

    @XmlElement
    private CarErrorType error;

    public enum CarErrorType {
        AirplaneAlreadyExists,
        InternalError
    }
}
