package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class DataSourceComponentFactoryTest {

    @Test
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    void createComponent() throws NoSuchFieldException, IllegalAccessException {
        final var resolver = mock(ComponentResolver.class);
        final var properties = mock(Environment.class);
        doReturn("url").when(properties).getProperty("datasource.url");
        doReturn("password").when(properties).getProperty("datasource.password");
        doReturn("username").when(properties).getProperty("datasource.username");

        final var factoryComponent = new DataSourceComponentFactory();
        final var dataSource = factoryComponent.createComponent(properties, resolver);

        final var sqlDataSource = assertInstanceOf(SQLServerDataSource.class, dataSource);
        assertEquals("url", sqlDataSource.getURL());
        assertEquals("username", sqlDataSource.getUser());
        final var connectionPropsField = SQLServerDataSource.class.getDeclaredField("connectionProps");
        connectionPropsField.setAccessible(true);
        final var conProperties = (Properties) connectionPropsField.get(sqlDataSource);

        assertEquals("password", conProperties.getProperty("password"));
    }
}
