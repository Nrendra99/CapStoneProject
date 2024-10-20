package org.user.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.user.app.dto.AuthRequest;
import org.user.app.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@Tag(name = "Authentication Controller", description = "Endpoints for user authentication")
public class LoginController {

    @Autowired
    private AuthService authService; // Service for authentication operations

    @GetMapping("/login")
    @Operation(summary = "Show the login page")
    public String showLoginPage(@RequestParam(required = false) String error, Model model) {
    	 if (error != null) {
    	        model.addAttribute("ErrorMessage", error);
    	    }
        return "login"; 
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and redirect based on role")
    public String authenticateUser(@RequestParam String email, @RequestParam String password, 
                                   HttpServletResponse response, Model model) {
        try {
            AuthRequest authRequest = new AuthRequest();
            authRequest.setEmail(email); // Set email in the authentication request
            authRequest.setPassword(password); // Set password in the authentication request

            // Call the service method to handle authentication and token generation
            authService.authenticateAndGenerateToken(authRequest, response);
            
            // Check the role of the user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getAuthorities() != null) {
                boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

                // Redirect based on user role
                if (isAdmin) {
                    return "redirect:/admin/home"; // Redirect to Admin home page
                } else {
                    return "redirect:/users/home"; // Redirect to Normal user home page
                }
            }

            // Fallback to a default page if role check fails
            return "redirect:/login";
        } catch (BadCredentialsException ex) {
            // Add error message to the model and return to the login page
            model.addAttribute("errorMessage", "Invalid credentials"); // Set error message
            return "login"; // Return to the login page with an error message
        } 
    }
}
