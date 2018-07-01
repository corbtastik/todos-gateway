package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ApiPreFilter extends ZuulFilter {

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
        RequestContext context = RequestContext.getCurrentContext();
        context.set("api.mode", "default");
        context.set("api.request.start", System.currentTimeMillis());
        return null;
    }
}
