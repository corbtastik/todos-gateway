package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class CqrsModePreFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CqrsModePreFilter.class);

    private Environment env;

    public CqrsModePreFilter(@Autowired Environment env) {
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

        if(!context.getRequest().getRequestURI().startsWith("/cqrs")) {
            return false;
        }

        return false;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String method = context.getRequest().getMethod().toUpperCase();
        switch(method) {
            // C.U.D
            case "POST": {
                LOG.debug("POST serviceId todos-command");
                context.set("serviceId", "todos-command");
                break;
            }
            case "PATCH": {
                LOG.debug("PATCH serviceId todos-command");
                context.set("serviceId", "todos-command");
                break;
            }
            case "DELETE": {
                LOG.debug("DELETE serviceId todos-command");
                context.set("serviceId", "todos-command");
                break;
            }
            // R
            case "GET": {
                LOG.debug("GET serviceId todos-query");
                context.set("serviceId", "todos-query");
                break;
            }
            default: {
                LOG.debug("method not supported " + method);
            }
        }
        return null;
    }
}
