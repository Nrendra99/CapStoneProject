package org.user.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
	
	// Inherits CRUD operations for Seat entities

}
