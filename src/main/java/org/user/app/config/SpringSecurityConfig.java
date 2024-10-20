package org.user.app.config;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.user.app.entry.JwtAuthenticationEntryPoint;
import org.user.app.jwt.JwtFilter;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SpringSecurityConfig {

    @Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;
    
    // Password encoder bean using BCrypt
    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter authenticationFilter) throws Exception {

        http
            // Use stateless session management for JWT-based security
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests((authorize) -> {
                // Allow public access to Swagger docs, login endpoints, and user registration
                authorize.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/login", "login.html", "/users/add").permitAll();
                
                // Permit preflight OPTIONS requests for CORS
                authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                
                // Allow USER role access to specific endpoints
                authorize.requestMatchers("/users/**", "/tickets/**").hasRole("USER");

                // Restrict all other endpoints to ADMIN role
                authorize.anyRequest().hasRole("ADMIN");
            })
            // Disable CSRF as it's not needed for JWT-based security
            .csrf(csrf -> csrf.disable())
            // Disable HTTP Basic Authentication
            .httpBasic(Customizer.withDefaults())
            // Custom entry point for unauthorized access
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
            );

        // Add the custom JWT authentication filter before the username-password authentication filter
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure custom logout handler
        http
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();  // Build and return the SecurityFilterChain
    }

    // Authentication manager bean for handling authentication logic
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}