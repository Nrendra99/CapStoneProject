package org.user.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.user.app.dto.AuthRequest;
import org.user.app.jwt.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public void authenticateAndGenerateToken(AuthRequest authRequest, HttpServletResponse response) {
        try {
            // Perform authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token using JwtTokenProvider
            String token = jwtTokenProvider.generateToken(authentication);

            // Set the JWT token in an HTTP-only cookie
            Cookie cookie = new Cookie("accessToken", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7200);
            response.addCookie(cookie);

        } catch (AuthenticationException ex) {
            // Handle authentication failure
            throw new BadCredentialsException("Invalid username or password.", ex);
        }
    }
}