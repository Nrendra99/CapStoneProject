package org.user.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Admin;

@Repository 
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByEmail(String email); // Retrieves an Admin by their email, returning an Optional
}