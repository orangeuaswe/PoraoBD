package com.bd.porao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
@Data
@Entity
@Table(name="messages")
public class Message
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant sentAt = Instant.now();
}
