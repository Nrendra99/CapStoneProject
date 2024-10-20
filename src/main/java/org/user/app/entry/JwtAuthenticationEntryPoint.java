package org.user.app.entry;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Set the response status to unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Redirect to the login page with a message
        response.sendRedirect(request.getContextPath() + "/login?error=" + authException.getMessage());
    }
}