package com.booking.ticketbooking.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventSeat> eventSeats = new ArrayList<>();

    public Event() {
    }

    public Event(String name, String description, String venue) {
        this.name = name;
        this.description = description;
        this.venue = venue;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public List<EventSeat> getEventSeats() {
        return eventSeats;
    }

    public void setEventSeats(List<EventSeat> eventSeats) {
        this.eventSeats = eventSeats;
    }
}