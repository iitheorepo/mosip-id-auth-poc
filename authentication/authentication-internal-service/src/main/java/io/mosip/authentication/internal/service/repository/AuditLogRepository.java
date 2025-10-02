package io.mosip.authentication.internal.service.repository;

import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, String> {
}
