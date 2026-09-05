package com.booking.ticketbooking.dto;

public record EventResponse(
        Long id,
        String name,
        String description,
        String venue
) {
}
