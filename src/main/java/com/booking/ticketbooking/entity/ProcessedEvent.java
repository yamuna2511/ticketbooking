package com.booking.ticketbooking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    private Long eventId;

    public ProcessedEvent() {
    }

    public ProcessedEvent(Long eventId) {
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}