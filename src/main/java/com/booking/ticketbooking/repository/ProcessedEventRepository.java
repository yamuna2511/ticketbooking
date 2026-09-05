package com.booking.ticketbooking.repository;

import com.booking.ticketbooking.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO processed_events(event_id)
            VALUES (:eventId)
            ON CONFLICT (event_id) DO NOTHING
            """, nativeQuery = true)
    int tryMarkProcessed(@Param("eventId") Long eventId);
}