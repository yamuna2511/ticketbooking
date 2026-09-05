package com.booking.ticketbooking.kafka;

import com.booking.ticketbooking.event.BookingConfirmedEvent;
import com.booking.ticketbooking.service.BookingEventProcessor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class BookingEventConsumer {

    private final BookingEventProcessor bookingEventProcessor;

    public BookingEventConsumer(
            BookingEventProcessor bookingEventProcessor) {

        this.bookingEventProcessor = bookingEventProcessor;

        System.out.println(">>> BookingEventConsumer CREATED");
    }

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000),
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = "booking-events",
            groupId = "notification-service"
    )
    public void consume(BookingConfirmedEvent event) {

        bookingEventProcessor.process(event);
    }
}