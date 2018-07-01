package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class ApiPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 200;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.getRequest().getRequestURI().startsWith("/api");
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        Long start = (Long)context.get("api.request.start");
        context.set("api.request.end", System.currentTimeMillis());
        Long stop = (Long)context.get("api.request.end");
        context.set("api.request.duration", stop - start);
        HttpServletResponse servletResponse = context.getResponse();
        servletResponse.addHeader("X-TODOS-GATEWAY-REQUEST-ID", UUID.randomUUID().toString());
        servletResponse.addHeader("X-TODOS-GATEWAY-REQUEST-DURATION-MS", String.valueOf(stop - start));
        servletResponse.addHeader("X-TODOS-GATEWAY-API-MODE", (String)context.get("api.mode"));
        return null;
    }
}
