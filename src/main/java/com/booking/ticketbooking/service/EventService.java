package com.booking.ticketbooking.service;

import com.booking.ticketbooking.dto.EventRequest;
import com.booking.ticketbooking.dto.EventResponse;
import com.booking.ticketbooking.entity.Event;
import com.booking.ticketbooking.exception.EventNotFoundException;
import com.booking.ticketbooking.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse createEvent(EventRequest request) {

        Event event = new Event(
                request.name(),
                request.description(),
                request.venue()
        );

        Event savedEvent = eventRepository.save(event);

        return new EventResponse(
                savedEvent.getId(),
                savedEvent.getName(),
                savedEvent.getDescription(),
                savedEvent.getVenue()
        );
    }

    public List<EventResponse> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(event -> new EventResponse(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getVenue()
                ))
                .toList();
    }

    public EventResponse getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getVenue()
        );
    }
}