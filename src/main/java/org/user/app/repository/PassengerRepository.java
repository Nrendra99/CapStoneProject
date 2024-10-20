package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {
	// Inherits CRUD operations for Passenger entities
}
