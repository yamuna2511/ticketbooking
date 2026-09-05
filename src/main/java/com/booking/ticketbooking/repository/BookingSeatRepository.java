package com.booking.ticketbooking.repository;

import com.booking.ticketbooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
}