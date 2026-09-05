package com.booking.ticketbooking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SeatRequest(

        @NotBlank(message = "Seat number is required")
        String seatNumber,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price
) {
}