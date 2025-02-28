package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class DataSourceComponentFactoryTest {

    @SneakyThrows
    @Test
    void createComponent() {
        final var resolver = mock(ComponentResolver.class);
        final var properties = mock(Properties.class);
        doReturn("url").when(properties).getProperty("datasource.url");
        doReturn("password").when(properties).getProperty("datasource.password");
        doReturn("username").when(properties).getProperty("datasource.username");

        final var dataSourceComponentFactory = new DataSourceComponentFactory();
        final var dataSource = dataSourceComponentFactory.createComponent(properties, resolver);

        final var sqlDataSource = assertInstanceOf(SQLServerDataSource.class, dataSource);
        assertEquals("url", sqlDataSource.getURL());
        assertEquals("username", sqlDataSource.getUser());
        final var connectionPropsField = SQLServerDataSource.class.getDeclaredField("connectionProps");
        connectionPropsField.setAccessible(true);
        final var conProperties = (Properties) connectionPropsField.get(sqlDataSource);

        assertEquals("password", conProperties.getProperty("password"));
    }
}
