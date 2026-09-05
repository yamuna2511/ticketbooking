package com.booking.ticketbooking.exception;

public class SeatNotConfiguredException extends RuntimeException {

    public SeatNotConfiguredException(String message) {
        super(message);
    }
}