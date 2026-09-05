package com.booking.ticketbooking.exception;

public class PaymentNotFoundException
        extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }
}