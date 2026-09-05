package com.booking.ticketbooking.exception;

public class SeatBookingConflictException extends RuntimeException {

    public SeatBookingConflictException(String message) {
        super(message);
    }
}