package com.booking.ticketbooking.controller;

import com.booking.ticketbooking.dto.BookingRequest;
import com.booking.ticketbooking.dto.BookingResponse;
import com.booking.ticketbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @PathVariable Long eventId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody BookingRequest request) {

        return bookingService.createBooking(eventId, request, idempotencyKey);
    }
}