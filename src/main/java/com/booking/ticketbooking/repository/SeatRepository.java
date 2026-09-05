package com.booking.ticketbooking.repository;

import com.booking.ticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findBySeatNumber(String seatNumber);

}