package com.fistein.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String origin = request.getHeader("Origin");
        
        // Allow specific origins
        if (origin != null && (origin.equals("http://localhost:3000") || 
                               origin.equals("http://localhost:5173") || 
                               origin.equals("http://localhost:8081"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD");
        response.setHeader("Access-Control-Allow-Headers", 
                "Origin,X-Requested-With,Content-Type,Accept,Authorization,Cache-Control,Pragma,Expires," +
                "sec-ch-ua,sec-ch-ua-mobile,sec-ch-ua-platform");
        response.setHeader("Access-Control-Allow-Credentials", "false");
        response.setHeader("Access-Control-Max-Age", "0");
        
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}