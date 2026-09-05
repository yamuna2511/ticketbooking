package com.booking.ticketbooking.service;

import com.booking.ticketbooking.dto.BookingRequest;
import com.booking.ticketbooking.dto.BookingResponse;
import com.booking.ticketbooking.dto.BookingSeatResponse;
import com.booking.ticketbooking.entity.*;
import com.booking.ticketbooking.exception.EventNotFoundException;
import com.booking.ticketbooking.exception.SeatNotAvailableException;
import com.booking.ticketbooking.exception.SeatNotConfiguredException;
import com.booking.ticketbooking.repository.BookingRepository;
import com.booking.ticketbooking.repository.EventRepository;
import com.booking.ticketbooking.repository.EventSeatRepository;
import com.booking.ticketbooking.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final EventRepository eventRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;

    public BookingService(
            EventRepository eventRepository,
            EventSeatRepository eventSeatRepository,
            BookingRepository bookingRepository,
            PaymentService paymentService) {

        this.eventRepository = eventRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingRepository = bookingRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public BookingResponse createBooking(
            Long eventId,
            BookingRequest request,
            String idempotencyKey) {

        Optional<Booking> existingBooking =
                bookingRepository.findByIdempotencyKey(idempotencyKey);

        if (existingBooking.isPresent()) {
            return toBookingResponse(existingBooking.get());
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<EventSeat> eventSeats = new ArrayList<>();

        for (String seatNumber : request.seatNumbers()) {

            EventSeat eventSeat =
                    eventSeatRepository
                            .findByEventIdAndSeat_SeatNumber(
                                    eventId,
                                    seatNumber
                            )
                            .orElseThrow(() ->
                                    new SeatNotConfiguredException(
                                            "Seat " + seatNumber
                                                    + " is not configured for this event"
                                    )
                            );

            eventSeat.hold(Duration.ofMinutes(5));

            eventSeats.add(eventSeat);
        }

        BigDecimal totalAmount = eventSeats.stream()
                .map(EventSeat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Booking booking = new Booking(event, totalAmount, idempotencyKey);

        booking = bookingRepository.save(booking);


        for (EventSeat eventSeat : eventSeats) {

            BookingSeat bookingSeat = new BookingSeat(
                    booking,
                    eventSeat,
                    eventSeat.getPrice()
            );

            booking.getBookingSeats().add(bookingSeat);

        }

        Payment payment = paymentService.createPayment(
                booking,
                totalAmount
        );

        return toBookingResponse(booking);
    }

    private BookingResponse toBookingResponse(Booking booking) {

        List<BookingSeatResponse> seatResponses =
                booking.getBookingSeats()
                        .stream()
                        .map(bookingSeat ->
                                new BookingSeatResponse(
                                        bookingSeat
                                                .getEventSeat()
                                                .getSeat()
                                                .getSeatNumber(),
                                        bookingSeat.getPrice()
                                )
                        )
                        .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getEvent().getName(),
                seatResponses,
                booking.getTotalAmount(),
                booking.getStatus().name()
        );
    }
}