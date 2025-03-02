# Flight Management System

This project is developed using Gradle as the build tool and follows a layered architecture comprising Model, Repository, Service, and UI layers, designed to embrace Java's OOP principles.

The core functionality revolves around managing flight operations, including the ability to:

* Add an airplane
* Add a flight
* Add a scheduled flight
* Add a booking

Data is stored either:
* in [memory](application-context/src/main/java/ro/eduardismund/flightmgmt/repo/InmemFlightManagementRepository.java)
  * with [file serialization](application-context/src/main/java/ro/eduardismund/flightmgmt/repo/JavaSerializationFlightManagementPersistenceManager.java), demonstrating the persistence of information through serialization and deserialization processes.
* in [RDBMS](application-context/src/main/java/ro/eduardismund/flightmgmt/repo/JdbcFlightManagementRepository.java)
  * in order to initialize MS SQL Server Database run the scripts: [database.sql](sql/database.sql), [schema.sql](sql/schema.sql)

Gradle integrates tools like Checkstyle, PMD, Spotless, and SpotBugs to ensure code quality, adherence to standards, and maintainability.

The project features comprehensive testing with 100% coverage. Mocking is utilized to independently verify each functionality, while Pitest is employed for mutation testing, ensuring robust feature verification and improving code reliability.

The [build.gradle](build.gradle) has the following features:
* __Plugins__: Includes necessary plugins for building, testing, code analysis, and formatting: java, application, jacoco, checkstyle, pmd, Pitest, Spotless, SpotBugs.
* __Pitest__: Configured for mutation testing with a 100% mutation coverage , targeting the specified classes and generating reports in HTML format.
* __Dependencies__: Includes __Lombok__ for boilerplate reduction, JUnit and Mockito for testing, and FindBugs for static analysis.
* __Testing__: Configures __JUnit 5__ for testing and ensures code coverage reporting via JaCoCo.
* __Checkstyle__ and __PMD__: Configured for static code analysis, with PMD allowing failures to be ignored.
* __SpotBugs__: Configures HTML report generation for both main and test tasks.
* __Spotless__: Ensures consistent code formatting using Palantir’s Java formatter and removes unused imports.
* __JaCoCo__: Excludes certain directories from coverage reports and enforces 100% coverage verification.#

# Features: 
* Application Context
* Socket Client
* Socket Server
* Jdbc Repository
* In memory repository
* Domain core
  * Service interface
  * repository interface
  * core model
* transport model
* cli