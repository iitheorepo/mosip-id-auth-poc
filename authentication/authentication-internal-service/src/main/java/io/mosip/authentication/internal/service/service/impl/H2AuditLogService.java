package io.mosip.authentication.internal.service.service.impl;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import io.mosip.authentication.internal.service.repository.AuditLogRepository;
import io.mosip.authentication.internal.service.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class H2AuditLogService implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Log an audit event to H2 database
     *
     * @param request the audit log request
     * @return AuditLogResponseDto with event ID and timestamp
     */
    @Override
    public AuditLogResponseDto logEvent(AuditLogRequestDto request) {
        String eventId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();

        EventType eventType = EventType.valueOf(request.getEventType());

        AuditLogEntity entity = new AuditLogEntity(
            eventId,
            eventType,
            request.getDescription(),
            request.getUserId(),
            timestamp
        );

        auditLogRepository.save(entity);

        return new AuditLogResponseDto(eventId, timestamp);
    }

    @Override
    public List<AuditLogEntity> getAllEvents() {
        return auditLogRepository.findAll();
    }

    /**
     * Get audit events with optional filtering by userId and/or eventType, with sorting
     *
     * @param userId filter by user ID (optional)
     * @param eventType filter by event type (optional)
     * @param sortBy field to sort by (default: timestamp)
     * @param sortOrder sort order (asc/desc, default: desc)
     * @return List of AuditLogEntity matching the criteria
     */
    @Override
    public List<AuditLogEntity> getEvents(String userId, EventType eventType, String sortBy, String sortOrder) {
        Sort sort = Sort.by(
            "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
            sortBy != null ? sortBy : "timestamp"
        );

        if (userId != null && eventType != null) {
            return auditLogRepository.findByUserIdAndEventType(userId, eventType, sort);
        } else if (userId != null) {
            return auditLogRepository.findByUserId(userId, sort);
        } else if (eventType != null) {
            return auditLogRepository.findByEventType(eventType, sort);
        } else {
            return auditLogRepository.findAll(sort);
        }
    }
}
