package org.springframework.ai.mcp.sample.server;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(Integer.MIN_VALUE) // Ensure this runs before Spring Security's filters
public class RedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().equals("/.well-known/oauth-authorization-server")) {
            System.out.println("Todo: Investigate, currently redirect to the custom endpoint as spring does not support scopes and registration endpoint in this version");
            res.sendRedirect("/custom-well-known/oauth-authorization-server");
            return;
        }

        chain.doFilter(request, response);
    }
}