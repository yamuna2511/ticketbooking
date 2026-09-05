package com.booking.ticketbooking.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookingRequest(

        @NotEmpty
        List<String> seatNumbers

) {
}