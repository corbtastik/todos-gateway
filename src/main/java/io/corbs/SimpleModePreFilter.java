package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SimpleModePreFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleModePreFilter.class);

    private Environment env;

    public SimpleModePreFilter(@Autowired Environment env) {
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
            String apiMode = this.env.getProperty("todos.api.mode");
            LOG.debug("todos.api.mode=" + apiMode);
            return "simple".equalsIgnoreCase(apiMode) || "".equalsIgnoreCase(apiMode);
        }

        return false;
    }

    @Override
    public Object run() {
        LOG.debug("SimpleModePreFilter.run() api.mode=simple calling serviceId todos-api");
        RequestContext context = RequestContext.getCurrentContext();
        context.set("serviceId", "todos-api");
        context.set("api.mode", "simple");
        return null;
    }
}
