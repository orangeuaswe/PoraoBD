package com.bd.porao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "availability_slots")
public class AvailabilitySlot
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private User tutor;

    private Instant startUtc;
    private Instant endUtc;
    private boolean recurringWeekly = false;
}