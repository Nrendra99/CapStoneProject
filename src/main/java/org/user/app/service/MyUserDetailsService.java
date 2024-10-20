package org.user.app.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.user.app.entity.Admin;
import org.user.app.entity.User;
import org.user.app.repository.AdminRepository;
import org.user.app.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check if the user is in the User repository
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(), 
                    user.getPassword(), 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        // If not found in User, check Admin repository
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(), 
                    admin.getPassword(), 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // If no match found, throw exception
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}