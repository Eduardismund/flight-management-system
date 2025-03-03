package ro.eduardismund.flightmgmt.app;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;
import ro.eduardismund.appctx.ComponentFactory;
import ro.eduardismund.appctx.ComponentResolver;
import ro.eduardismund.appctx.Environment;

/**
 * Factory for creating a {@link DataSource} using environment properties.
 */
public class DataSourceComponentFactory implements ComponentFactory<DataSource> {

    /**
     * Creates a {@link DataSource} configured with the provided environment properties.
     *
     * @param properties The environment properties containing database connection details.
     * @param componentResolver The component resolver.
     * @return A {@link DataSource} instance configured with the given properties.
     */
    @Override
    public DataSource createComponent(Environment properties, ComponentResolver componentResolver) {
        final String url = properties.getProperty("datasource.url");
        final String username = properties.getProperty("datasource.username");
        final String password = properties.getProperty("datasource.password");

        final var dataSource = new SQLServerDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
