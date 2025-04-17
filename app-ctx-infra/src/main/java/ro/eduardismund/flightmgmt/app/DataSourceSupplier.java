package ro.eduardismund.flightmgmt.app;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 * Factory for creating a {@link DataSource} using environment properties.
 */
@RequiredArgsConstructor
public class DataSourceSupplier implements Supplier<DataSource> {

    private final ApplicationContext applicationContext;

    /**
     * Creates a {@link DataSource} configured with the provided environment properties.
     *
     * @return A {@link DataSource} instance configured with the given properties.
     */
    @Override
    public DataSource get() {
        final String url = applicationContext.getEnvironment().getRequiredProperty("datasource.url");
        final String username = applicationContext.getEnvironment().getRequiredProperty("datasource.username");
        final String password = applicationContext.getEnvironment().getRequiredProperty("datasource.password");

        final var dataSource = new SQLServerDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
