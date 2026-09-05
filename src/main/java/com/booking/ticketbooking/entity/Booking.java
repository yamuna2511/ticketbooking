package com.booking.ticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @OneToOne(
            mappedBy = "booking",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Payment payment;

    protected Booking() {
    }

    public Booking(Event event, BigDecimal totalAmount, String idempotencyKey) {
        this.event = event;
        this.totalAmount = totalAmount;
        this.idempotencyKey = idempotencyKey;
        this.status = BookingStatus.PENDING_PAYMENT;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<BookingSeat> getBookingSeats() {
        return bookingSeats;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Payment getPayment() {
        return payment;
    }

    public void attachPayment(Payment payment) {
        this.payment = payment;
    }

    public void confirm() {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Booking cannot be confirmed from status " + status
            );
        }

        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Booking cannot be cancelled from status " + status
            );
        }

        this.status = BookingStatus.CANCELLED;
    }
}