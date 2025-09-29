package com.cartify.authservice.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestIdFilter implements Filter {
    private static final String HDR = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String rid = req.getHeader(HDR);
        if (rid == null || rid.isBlank()) rid = UUID.randomUUID().toString();

        MDC.put(MDC_KEY, rid);
        res.setHeader(HDR, rid);

        try { chain.doFilter(request, response); }
        finally { MDC.remove(MDC_KEY); }
    }
}