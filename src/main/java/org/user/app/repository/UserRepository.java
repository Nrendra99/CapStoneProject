package org.user.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> {

    // Retrieves an Optional User by their email address
    Optional<User> findByEmail(String email);
}
