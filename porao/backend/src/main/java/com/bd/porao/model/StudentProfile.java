package com.bd.porao.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "student_profiles")
public class StudentProfile
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String gradeLevel;

    @Column(columnDefinition = "TEXT")
    private String interests;
}
