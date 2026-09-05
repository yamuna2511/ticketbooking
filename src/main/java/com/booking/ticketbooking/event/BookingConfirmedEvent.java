package com.booking.ticketbooking.event;

import java.math.BigDecimal;

public record BookingConfirmedEvent(
        Long bookingId,
        Long eventId,
        String eventName,
        BigDecimal totalAmount
) {
}