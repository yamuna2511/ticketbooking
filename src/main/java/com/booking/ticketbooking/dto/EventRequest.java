package com.booking.ticketbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EventRequest(

        @NotBlank(message = "Event name is required")
        @Size(max = 100, message = "Event name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Venue is required")
        @Size(max = 100, message = "Venue must not exceed 100 characters")
        String venue
) {
}