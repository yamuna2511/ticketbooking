package com.booking.ticketbooking.kafka;

import com.booking.ticketbooking.event.BookingConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingEventProducer {

    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    public BookingEventProducer(
            KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingConfirmed(
            BookingConfirmedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.bookingId().toString(),
                event
        );
    }
}