package com.booking.ticketbooking.dto;

public record PaymentWebhookRequest(
        String paymentReference,
        String status
) {
}