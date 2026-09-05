package com.booking.ticketbooking.controller;

import com.booking.ticketbooking.dto.EventSeatResponse;
import com.booking.ticketbooking.entity.EventSeat;
import com.booking.ticketbooking.dto.SeatRequest;
import com.booking.ticketbooking.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventSeatResponse createSeat(
            @PathVariable Long eventId,
            @Valid @RequestBody SeatRequest request) {

        return seatService.createSeat(eventId, request);
    }

    @GetMapping
    public List<EventSeatResponse> getEventSeats(@PathVariable Long eventId) {
        return seatService.getSeatsByEventId(eventId);
    }
}