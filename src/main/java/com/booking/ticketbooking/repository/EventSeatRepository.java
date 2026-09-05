package com.booking.ticketbooking.repository;

import com.booking.ticketbooking.entity.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    boolean existsByEventIdAndSeatId(Long eventId, Long seatId);

    List<EventSeat> findByEventId(Long eventId);

    Optional<EventSeat> findByEventIdAndSeat_SeatNumber(
            Long eventId,
            String seatNumber
    );

    @Modifying
    @Query("""
        UPDATE EventSeat es
        SET es.status = com.booking.ticketbooking.entity.SeatStatus.BOOKED
        WHERE es.id = :eventSeatId
        AND es.status = com.booking.ticketbooking.entity.SeatStatus.HELD
        """)
    int bookIfAvailable(@Param("eventSeatId") Long eventSeatId);
}