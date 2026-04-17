package com.telegramapproval.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 10)
    private ActionType actionType;

    /**
     * JSON payload containing the user data for the requested action.
     * ADD:    {"name":"...", "email":"..."}
     * UPDATE: {"userId":5, "name":"...", "email":"..."}
     * DELETE: {"userId":5, "name":"...", "email":"..."}
     */
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    /**
     * The Telegram message_id returned after sending the approval message.
     * Used to edit the message after approve/reject.
     */
    @Column(name = "telegram_message_id")
    private Integer telegramMessageId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
    }
}
