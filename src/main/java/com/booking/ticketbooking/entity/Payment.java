package com.booking.ticketbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_booking",
                        columnNames = "booking_id"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String paymentReference;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public Payment(Booking booking, BigDecimal amount, String paymentReference) {
        this.booking = booking;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.paymentReference = paymentReference;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markSuccessful() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
}