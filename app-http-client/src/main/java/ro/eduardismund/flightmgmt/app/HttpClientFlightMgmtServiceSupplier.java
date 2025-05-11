package ro.eduardismund.flightmgmt.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.HttpClientFlightMgmtService;

/**
 * Factory for {@link HttpClientFlightMgmtService}.
 */
@RequiredArgsConstructor
public class HttpClientFlightMgmtServiceSupplier implements Supplier<HttpClientFlightMgmtService> {

    final ApplicationContext applicationContext;

    /**
     * Create {@link HttpClientFlightMgmtService}.
     *
     * @return new instance of {@link HttpClientFlightMgmtService}
     */
    @SneakyThrows
    @Override
    public HttpClientFlightMgmtService get() {
        return new HttpClientFlightMgmtService(
                HttpClient.newHttpClient(),
                new URI(applicationContext.getEnvironment().getRequiredProperty("restApiBaseUri")),
                applicationContext.getBean(XmlManager.class),
                applicationContext.getBean(DomainMapper.class));
    }
}
