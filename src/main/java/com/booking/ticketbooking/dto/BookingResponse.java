package com.booking.ticketbooking.dto;

import java.math.BigDecimal;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String eventName,
        List<BookingSeatResponse> seats,
        BigDecimal totalAmount,
        String status
) {
}