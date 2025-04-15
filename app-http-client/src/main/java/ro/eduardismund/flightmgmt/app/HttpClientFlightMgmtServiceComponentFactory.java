package ro.eduardismund.flightmgmt.app;

import com.github.eduardismund.appctx.ComponentFactory;
import com.github.eduardismund.appctx.ComponentResolver;
import com.github.eduardismund.appctx.Environment;
import java.net.URI;
import java.net.http.HttpClient;
import lombok.SneakyThrows;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.HttpClientFlightMgmtService;

/**
 * Factory for {@link HttpClientFlightMgmtService}.
 */
public class HttpClientFlightMgmtServiceComponentFactory implements ComponentFactory<HttpClientFlightMgmtService> {

    /**
     * Create  {@link HttpClientFlightMgmtService}.
     *
     * @param properties application context properties
     * @param resolver   application context components resolver
     * @return new instance of  {@link HttpClientFlightMgmtService}
     */
    @SneakyThrows
    @Override
    public HttpClientFlightMgmtService createComponent(Environment properties, ComponentResolver resolver) {
        return new HttpClientFlightMgmtService(
                HttpClient.newHttpClient(),
                new URI(properties.getProperty("restApiBaseUri")),
                resolver.resolveComponent(XmlManager.class),
                resolver.resolveComponent(DomainMapper.class));
    }
}
