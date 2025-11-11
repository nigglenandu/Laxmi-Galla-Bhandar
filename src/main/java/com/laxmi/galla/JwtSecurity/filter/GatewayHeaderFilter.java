package com.laxmi.galla.JwtSecurity.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GatewayHeaderFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String gatewayHeader = httpRequest.getHeader("X-Gateway-Auth");

        if (gatewayHeader == null || !gatewayHeader.equals("true")) {
            throw new ServletException("Unauthorized access: must go through Gateway");
        }

        chain.doFilter(request, response);
    }
}