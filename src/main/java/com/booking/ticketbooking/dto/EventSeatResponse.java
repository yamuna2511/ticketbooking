package com.booking.ticketbooking.dto;

import com.booking.ticketbooking.entity.SeatStatus;

import java.math.BigDecimal;

public record EventSeatResponse(
        Long id,
        String seatNumber,
        String eventName,
        BigDecimal price,
        SeatStatus status
) {}