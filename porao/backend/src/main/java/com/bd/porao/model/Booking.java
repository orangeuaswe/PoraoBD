package com.bd.porao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
@Data
@Entity
@Table(name="bookings")
public class Booking
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private User tutor;

    private Instant startUtc;
    private Instant endUtc;
    private String status;
    private String paymentRef;
    private Double amount;
    private Instant createdAt = Instant.now();
}
