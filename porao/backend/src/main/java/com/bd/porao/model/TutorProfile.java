package com.bd.porao.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tutor_profiles")
public class TutorProfile
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String headline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String subject;
    private String languages;
    private Double ratePerHour;
    private Double lat;
    private Double lng;
    private Double rating = 0.0;
    private Integer ratingCount = 0;

    @Column(columnDefinition = "TEXT")
    private String embeddingCache;
}
