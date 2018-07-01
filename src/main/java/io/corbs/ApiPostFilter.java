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
        return 100;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getRouteHost() != null &&
            RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse response = context.getResponse();
        response.addHeader("X-TODOS-GATEWAY-ID", UUID.randomUUID().toString());
        return null;
    }
}
