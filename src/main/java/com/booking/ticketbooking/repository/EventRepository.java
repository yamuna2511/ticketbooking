package com.booking.ticketbooking.repository;

import com.booking.ticketbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}