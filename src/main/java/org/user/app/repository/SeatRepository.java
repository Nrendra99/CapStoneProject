package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
	 // Inherits CRUD operations for Seat entities
}
