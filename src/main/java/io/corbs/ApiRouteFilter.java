package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.netflix.zuul.context.RequestContext.getCurrentContext;

@Component
public class ApiRouteFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiRouteFilter.class);

    @Autowired
    private final DiscoveryClient discoveryClient;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final TodosAPIClient apiClient;

    public ApiRouteFilter(@Autowired DiscoveryClient discoveryClient,
                          @Autowired RestTemplate restTemplate,
                          @Autowired TodosAPIClient apiClient) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
        this.apiClient = apiClient;
    }

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 99;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getRouteHost() != null &&
            RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() {
        RequestContext context = getCurrentContext();
        final String requestURI = context.getRequest().getRequestURI();
        LOG.debug("run() requestURI=" + requestURI);
        for(String serviceId : discoveryClient.getServices()) {
            logServiceInstance(discoveryClient.getInstances(serviceId));

        }
        if ("/api".equals(requestURI)
                && context.getRequest() != null
                && !StringUtils.isBlank(context.getRequest().getMethod())) {
            switch (context.getRequest().getMethod().toUpperCase()) {
                case "POST" : {
                    LOG.debug("post()");
                    context.set("serviceId", "todos-api");
                    context.setRouteHost(null);
                    context.addOriginResponseHeader("X-Zuul-ServiceId", "todos-gateway");
                    break;
                }
                case "GET" : {
                    LOG.debug("get()");

                    context.set("serviceId", "todos-api");
                    try {
                        context.setRouteHost(new URL("http://localhost:8080/todos/"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
//                    ResponseEntity<List> responseEntity = this.restTemplate
//                            .getForEntity("http://todos-api/todos/", List.class);

                    context.addOriginResponseHeader("X-Zuul-ServiceId", "todos-gateway");
                    break;
                }
                case "PATCH" : {
                    LOG.debug("patch()");
                    context.set("serviceId", "todos-api");
                    context.setRouteHost(null);
                    context.addOriginResponseHeader("X-Zuul-ServiceId", "todos-gateway");
                    break;
                }
                case "DELETE" : {
                    LOG.debug("delete()");
                    context.set("serviceId", "todos-api");
                    context.setRouteHost(null);
                    context.addOriginResponseHeader("X-Zuul-ServiceId", "todos-gateway");
                    break;
                }
                default : {
                    LOG.debug("No match for " + context.getRequest().getMethod().toUpperCase());
                }
            }

        }
        return null;
    }

    static void logServiceInstance(List<ServiceInstance> instances) {
        for(ServiceInstance instance : instances) {
            LOG.debug("@serviceInstance");
            LOG.debug("serviceId=" + instance.getServiceId());
            LOG.debug("uri=" + instance.getUri());
            LOG.debug("host=" + instance.getHost());
            LOG.debug("scheme=" + instance.getScheme());
            LOG.debug("port=" + instance.getPort());
            LOG.debug("metadata=");
            instance.getMetadata().forEach((key, value) -> LOG.debug(key + "=" + value));
        }
    }
}
