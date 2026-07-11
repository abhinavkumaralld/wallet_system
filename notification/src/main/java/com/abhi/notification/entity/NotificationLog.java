package com.abhi.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String referenceId;

    @Column(nullable = false)
    private String type; // EMAIL,SMS

    @Column(nullable = false)
    private String status; // SENT , FAILED
    private LocalDateTime processedAt;

    @PrePersist
    public void pre(){
        this.processedAt=LocalDateTime.now();
    }
}