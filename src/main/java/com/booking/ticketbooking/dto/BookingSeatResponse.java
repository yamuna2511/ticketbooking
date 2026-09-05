package com.booking.ticketbooking.dto;

import java.math.BigDecimal;

public record BookingSeatResponse(
        String seatNumber,
        BigDecimal price
) {
}