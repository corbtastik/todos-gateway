package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class DefaultModePreFilter extends ZuulFilter {

    private Environment env;

    public DefaultModePreFilter(@Autowired Environment env) {
        this.env = env;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 101;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();

        if(!context.getRequest().getRequestURI().startsWith("/api")) {
            return false;
        }

        if(this.env.containsProperty("todos.api.mode")) {
            if("simple".equalsIgnoreCase(this.env.getProperty("todos.api.mode").trim())) {
                return true;
            }
            return "".equals(this.env.getProperty("todos.api.mode").trim());
        }

        return false;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        context.set("serviceId", "todos-api");
        context.set("api.mode", "simple");
        return null;
    }
}
