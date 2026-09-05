package com.booking.ticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "booking_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_event_seat",
                        columnNames = {"booking_id", "event_seat_id"}
                )
        }
)
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_seat_id", nullable = false)
    private EventSeat eventSeat;

    @Column(nullable = false)
    private BigDecimal price;

    protected BookingSeat() {
    }

    public BookingSeat(
            Booking booking,
            EventSeat eventSeat,
            BigDecimal price) {

        this.booking = booking;
        this.eventSeat = eventSeat;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public EventSeat getEventSeat() {
        return eventSeat;
    }

    public BigDecimal getPrice() {
        return price;
    }
}