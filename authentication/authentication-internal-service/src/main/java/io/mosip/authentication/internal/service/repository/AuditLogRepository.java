package io.mosip.authentication.internal.service.repository;

import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, String> {

    List<AuditLogEntity> findByUserId(String userId, Sort sort);

    List<AuditLogEntity> findByEventType(EventType eventType, Sort sort);

    List<AuditLogEntity> findByUserIdAndEventType(String userId, EventType eventType, Sort sort);
}
