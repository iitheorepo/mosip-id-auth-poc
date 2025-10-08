package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import io.mosip.authentication.internal.service.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "audit-log-controller", description = "Audit Log Controller")
@Validated
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Log a custom audit event
     *
     * @param request the audit log request
     * @return ResponseEntity with AuditLogResponseDto
     */
    @PostMapping(value = "/log",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Log Audit Event",
               description = "Logs a custom audit event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event logged successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<AuditLogResponseDto> logAuditEvent(
            @Valid @RequestBody AuditLogRequestDto request) {

        AuditLogResponseDto response = auditLogService.logEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get audit events with optional filtering and sorting
     *
     * @param userId filter by user ID (optional)
     * @param eventType filter by event type (optional)
     * @param sortBy field to sort by (default: timestamp)
     * @param sortOrder sort order - asc or desc (default: desc)
     * @return ResponseEntity with list of AuditLogEntity
     */
    @GetMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Audit Events",
               description = "Retrieves audit events with optional filtering by userId and/or eventType, with sorting support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<List<AuditLogEntity>> getAuditEvents(
            @Parameter(description = "Filter by user ID")
            @RequestParam(required = false) String userId,

            @Parameter(description = "Filter by event type (LOGIN, LOGOUT, ACCESS, CREATE, UPDATE, DELETE, AUTHENTICATION, AUTHORIZATION, OTHER)")
            @RequestParam(required = false) EventType eventType,

            @Parameter(description = "Field to sort by (e.g., timestamp, userId, eventType)")
            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,

            @Parameter(description = "Sort order: asc or desc")
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {

        List<AuditLogEntity> events = auditLogService.getEvents(userId, eventType, sortBy, sortOrder);

        return ResponseEntity.ok(events);
    }
}