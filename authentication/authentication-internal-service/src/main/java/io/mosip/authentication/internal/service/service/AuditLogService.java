package io.mosip.authentication.internal.service.service;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;

import java.util.List;


public interface AuditLogService {

    AuditLogResponseDto logEvent(AuditLogRequestDto request);
    List<AuditLogEntity> getAllEvents();
    List<AuditLogEntity> getEvents(String userId, EventType eventType, String sortBy, String sortOrder);
}
