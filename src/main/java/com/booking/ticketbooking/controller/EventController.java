package com.booking.ticketbooking.controller;

import com.booking.ticketbooking.dto.EventRequest;
import com.booking.ticketbooking.dto.EventResponse;
import com.booking.ticketbooking.service.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponse getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }
}