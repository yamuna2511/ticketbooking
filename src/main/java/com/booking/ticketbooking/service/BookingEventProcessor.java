package com.booking.ticketbooking.service;

import com.booking.ticketbooking.event.BookingConfirmedEvent;
import com.booking.ticketbooking.repository.ProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingEventProcessor {

    private final ProcessedEventRepository processedEventRepository;

    public BookingEventProcessor(
            ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void process(BookingConfirmedEvent event) {

        int inserted = processedEventRepository
                .tryMarkProcessed(event.eventId());

        if (inserted == 0) {

            System.out.println(
                    "Duplicate event ignored: "
                            + event.eventId()
            );

            return;
        }

        System.out.println(
                "Processing booking event: "
                        + event.eventId()
        );

        // Business processing goes here

        System.out.println(
                "Event processed successfully: "
                        + event.eventId()
        );
    }
}