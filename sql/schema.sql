if OBJECT_ID('Airplane', 'U') is not null drop table Airplane;
CREATE TABLE Airplane
(
    IdNumber   VARCHAR(50) PRIMARY KEY,
    Model      VARCHAR(50) NOT NULL,
    SeatsCount INT         NOT NULL
);

if OBJECT_ID('Flight', 'U') is not null drop table Flight;
CREATE TABLE Flight
(
    Number  VARCHAR(50) PRIMARY KEY,
    Company VARCHAR(50) NOT NULL
);

if OBJECT_ID('ScheduledFlight', 'U') is not null drop table ScheduledFlight;
CREATE TABLE ScheduledFlight
(
    Id               VARCHAR(50) PRIMARY KEY,
    AirplaneIdNumber VARCHAR(50) NOT NULL,
    FlightNumber     VARCHAR(50) NOT NULL,
    DepartureTime    DATETIME    NOT NULL,
    ArrivalTime      DATETIME    NOT NULL,
    DepartureDate    AS CAST(DepartureTime AS DATE) PERSISTED,
    FOREIGN KEY (AirplaneIdNumber) REFERENCES Airplane (IdNumber),
    FOREIGN KEY (FlightNumber) REFERENCES Flight (Number),
    UNIQUE (FlightNumber, DepartureDate)
);

if OBJECT_ID('Seat', 'U') is not null drop table Seat;
create Table Seat
(
    AirplaneIdNumber VARCHAR(50) NOT NULL,
    Row              INT         NOT NULL,
    SeatName         VARCHAR(50) NOT NULL,
    BusinessClass    BIT         NOT NULL,
    FOREIGN KEY (AirplaneIdNumber) REFERENCES Airplane (IdNumber),
    PRIMARY KEY (AirplaneIdNumber, Row, SeatName)

);


if OBJECT_ID('SeatBooking', 'U') is not null drop table SeatBooking;
CREATE TABLE SeatBooking
(
    FirstName         VARCHAR(50) NOT NULL,
    LastName          VARCHAR(50) NOT NULL,
    IdDocument        VARCHAR(50) NOT NULL,
    ScheduledFlightId VARCHAR(50) NOT NULL,
    SeatRow           INT         NOT NULL,
    SeatName          VARCHAR(50) NOT NULL,
    BusinessClass     BIT         NOT NULL,
    PRIMARY KEY (ScheduledFlightId, SeatName, SeatRow),
    FOREIGN KEY (ScheduledFlightId) REFERENCES ScheduledFlight (Id),
);

