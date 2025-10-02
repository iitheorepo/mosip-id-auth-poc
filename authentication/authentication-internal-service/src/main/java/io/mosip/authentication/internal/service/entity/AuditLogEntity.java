package io.mosip.authentication.internal.service.entity;

import io.mosip.authentication.internal.service.dto.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 255)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
