package com.booking.ticketbooking.service;

import com.booking.ticketbooking.dto.PaymentWebhookRequest;
import com.booking.ticketbooking.entity.*;
import com.booking.ticketbooking.event.BookingConfirmedEvent;
import com.booking.ticketbooking.exception.BookingNotFoundException;
import com.booking.ticketbooking.exception.PaymentNotFoundException;
import com.booking.ticketbooking.kafka.BookingEventProducer;
import com.booking.ticketbooking.repository.BookingRepository;
import com.booking.ticketbooking.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final BookingEventProducer bookingEventProducer;

    public PaymentService(PaymentRepository paymentRepository, BookingEventProducer bookingEventProducer) {
        this.paymentRepository = paymentRepository;
        this.bookingEventProducer = bookingEventProducer;
    }

    public Payment createPayment(
            Booking booking,
            BigDecimal amount) {

        String paymentReference =
                "PAY-" + booking.getId();

        Payment payment = new Payment(
                booking,
                amount,
                paymentReference
        );

        return paymentRepository.save(payment);
    }

    @Transactional
    public void processWebhook(
            PaymentWebhookRequest request) {

        Payment payment = paymentRepository
                .findByPaymentReference(
                        request.paymentReference()
                )
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment "
                                        + request.paymentReference()
                                        + " not found"
                        )
                );

        // Idempotency
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        if ("SUCCESS".equalsIgnoreCase(request.status())) {

            payment.markSuccessful();

            Booking booking = payment.getBooking();

            for (BookingSeat bookingSeat :
                    booking.getBookingSeats()) {

                bookingSeat
                        .getEventSeat()
                        .markBooked();
            }

            booking.confirm();

            bookingEventProducer.publishBookingConfirmed(
                    new BookingConfirmedEvent(
                            booking.getId(),
                            booking.getEvent().getId(),
                            booking.getEvent().getName(),
                            booking.getTotalAmount()
                    )
            );
        }
    }
}
