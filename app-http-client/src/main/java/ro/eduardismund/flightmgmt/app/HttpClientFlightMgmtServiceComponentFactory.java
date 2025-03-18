package ro.eduardismund.flightmgmt.app;

import lombok.SneakyThrows;
import ro.eduardismund.appctx.ComponentFactory;
import ro.eduardismund.appctx.ComponentResolver;
import ro.eduardismund.appctx.Environment;
import ro.eduardismund.flightmgmt.dtos.DomainMapper;
import ro.eduardismund.flightmgmt.dtos.XmlManager;
import ro.eduardismund.flightmgmt.service.HttpClientFlightMgmtService;

import java.net.URI;
import java.net.http.HttpClient;

public class HttpClientFlightMgmtServiceComponentFactory implements ComponentFactory<HttpClientFlightMgmtService> {
    @SneakyThrows
    @Override
    public HttpClientFlightMgmtService createComponent(Environment properties, ComponentResolver resolver) {
        return new HttpClientFlightMgmtService(HttpClient.newHttpClient(),
                new URI(properties.getProperty("restApiBaseUri")),
                resolver.resolveComponent(XmlManager.class),
                resolver.resolveComponent(DomainMapper.class));
    }
}
