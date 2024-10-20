package org.user.app.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
        throws ServletException, IOException {
        // Create a cookie with the name "accessToken" and set its value to null
        Cookie cookie = new Cookie("accessToken", null);
        // Set the cookie's age to 0 to instruct the browser to delete it
        cookie.setMaxAge(0);
        // Ensure the cookie applies to the root path of the domain
        cookie.setPath("/");
        // Add the cookie to the response
        response.addCookie(cookie);
        // Redirect the user to the login page with a "logout" parameter
        response.sendRedirect("/login?logout");
    }
}