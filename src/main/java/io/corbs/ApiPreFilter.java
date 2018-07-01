package io.corbs;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiPreFilter extends ZuulFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiPreFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 6;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        try {
            context.setRouteHost(new URL("http://httpbin.org"));
        } catch (MalformedURLException ex) {
            PrintWriter pw = new PrintWriter(new StringWriter());
            ex.printStackTrace(pw);
            LOG.error(pw.toString());
            return null;
        }
        return null;
    }
}
