package org.user.app.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import org.user.app.entity.Bus;

@Repository 
public interface BusRepository extends JpaRepository<Bus, Long> {
    // Inherits CRUD operations for Bus entities
}