package com.booking.ticketbooking.entity;

import com.booking.ticketbooking.exception.SeatNotAvailableException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "event_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_seat",
                        columnNames = {"event_id", "seat_id"}
                )
        }
)
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Version
    private Long version;

    @Column(name = "held_until")
    private LocalDateTime heldUntil;

    public EventSeat() {
    }

    public EventSeat(Event event, Seat seat, BigDecimal price, SeatStatus status) {
        this.event = event;
        this.seat = seat;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getHeldUntil() {
        return heldUntil;
    }

    public void hold(Duration duration) {
        releaseIfExpired();
        if (status != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seat.getSeatNumber() + " is not available"
            );
        }

        this.status = SeatStatus.HELD;
        this.heldUntil = LocalDateTime.now().plus(duration);
    }

    public void markBooked() {

        if (status != SeatStatus.HELD) {
            throw new IllegalStateException(
                    "Seat must be held before it can be booked"
            );
        }

        this.status = SeatStatus.BOOKED;
        this.heldUntil = null;
    }

    public void releaseIfExpired() {

        if (status == SeatStatus.HELD
                && heldUntil != null
                && !heldUntil.isAfter(LocalDateTime.now())) {

            this.status = SeatStatus.AVAILABLE;
            this.heldUntil = null;
        }
    }
}