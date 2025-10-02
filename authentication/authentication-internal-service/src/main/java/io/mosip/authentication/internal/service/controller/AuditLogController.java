package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
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
}