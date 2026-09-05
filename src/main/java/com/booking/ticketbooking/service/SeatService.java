package com.booking.ticketbooking.service;

import com.booking.ticketbooking.dto.EventSeatResponse;
import com.booking.ticketbooking.dto.SeatRequest;
import com.booking.ticketbooking.entity.Event;
import com.booking.ticketbooking.entity.EventSeat;
import com.booking.ticketbooking.entity.Seat;
import com.booking.ticketbooking.entity.SeatStatus;
import com.booking.ticketbooking.exception.DuplicateSeatException;
import com.booking.ticketbooking.exception.EventNotFoundException;
import com.booking.ticketbooking.repository.EventRepository;
import com.booking.ticketbooking.repository.EventSeatRepository;
import com.booking.ticketbooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final EventSeatRepository eventSeatRepository;

    public SeatService(
            SeatRepository seatRepository,
            EventRepository eventRepository,
            EventSeatRepository eventSeatRepository) {
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
        this.eventSeatRepository = eventSeatRepository;
    }

    @Transactional
    public EventSeatResponse createSeat(Long eventId, SeatRequest request) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        Seat seat = seatRepository.findBySeatNumber(request.seatNumber())
                .orElseGet(() ->
                        seatRepository.save(
                                new Seat(request.seatNumber())
                        )
                );

        if (eventSeatRepository.existsByEventIdAndSeatId(
                eventId,
                seat.getId())) {

            throw new DuplicateSeatException(
                    "Seat " + request.seatNumber()
                            + " is already configured for this event"
            );
        }

        EventSeat eventSeat = new EventSeat(
                event,
                seat,
                request.price(),
                SeatStatus.AVAILABLE
        );

        eventSeat = eventSeatRepository.save(eventSeat);

        return new EventSeatResponse(
                eventSeat.getId(),
                seat.getSeatNumber(),
                event.getName(),
                eventSeat.getPrice(),
                eventSeat.getStatus()
        );
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public List<EventSeatResponse> getSeatsByEventId(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<EventSeat> eventSeats =
                eventSeatRepository.findByEventId(eventId);

        return eventSeats.stream()
                .map(eventSeat -> new EventSeatResponse(
                        eventSeat.getId(),
                        eventSeat.getSeat().getSeatNumber(),
                        eventSeat.getEvent().getName(),
                        eventSeat.getPrice(),
                        eventSeat.getStatus()
                ))
                .toList();
    }
}