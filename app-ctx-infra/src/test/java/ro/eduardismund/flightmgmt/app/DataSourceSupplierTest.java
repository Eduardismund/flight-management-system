package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

class DataSourceSupplierTest {

    @Test
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    void get() throws NoSuchFieldException, IllegalAccessException {
        final var mockEnvironment = mock(Environment.class);
        final var mockAppContext = mock(ApplicationContext.class);
        doReturn(mockEnvironment).when(mockAppContext).getEnvironment();
        doReturn("url").when(mockEnvironment).getRequiredProperty("datasource.url");
        doReturn("password").when(mockEnvironment).getRequiredProperty("datasource.password");
        doReturn("username").when(mockEnvironment).getRequiredProperty("datasource.username");

        final var factoryComponent = new DataSourceSupplier(mockAppContext);
        final var dataSource = factoryComponent.get();

        final var sqlDataSource = assertInstanceOf(SQLServerDataSource.class, dataSource);
        assertEquals("url", sqlDataSource.getURL());
        assertEquals("username", sqlDataSource.getUser());
        final var connectionPropsField = SQLServerDataSource.class.getDeclaredField("connectionProps");
        connectionPropsField.setAccessible(true);
        final var conProperties = (Properties) connectionPropsField.get(sqlDataSource);

        assertEquals("password", conProperties.getProperty("password"));
    }
}
