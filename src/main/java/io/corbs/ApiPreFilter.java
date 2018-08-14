package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiPreFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiPreFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getRequest().getRequestURI().startsWith("/api");
    }

    @Override
    public Object run() {
        LOG.debug("ApiPreFilter.run()");
        RequestContext context = RequestContext.getCurrentContext();
        context.set("api.request.start", System.currentTimeMillis());
        return null;
    }
}
