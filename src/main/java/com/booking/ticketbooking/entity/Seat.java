package com.booking.ticketbooking.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false, unique = true)
    private String seatNumber;

    public Seat() {
    }

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}