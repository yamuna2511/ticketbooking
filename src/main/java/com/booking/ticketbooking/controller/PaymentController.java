package com.booking.ticketbooking.controller;

import com.booking.ticketbooking.dto.PaymentWebhookRequest;
import com.booking.ticketbooking.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody PaymentWebhookRequest request) {

        paymentService.processWebhook(request);

        return ResponseEntity.ok().build();
    }
}