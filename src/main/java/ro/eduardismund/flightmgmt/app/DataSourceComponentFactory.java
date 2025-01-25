package ro.eduardismund.flightmgmt.app;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.util.Properties;
import javax.sql.DataSource;

public class DataSourceComponentFactory implements ComponentFactory<DataSource> {
    @Override
    public DataSource createComponent(Properties properties, ComponentResolver componentResolver) {

        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(url);
        ds.setUser(username);
        ds.setPassword(password);

        return ds;
    }
}
