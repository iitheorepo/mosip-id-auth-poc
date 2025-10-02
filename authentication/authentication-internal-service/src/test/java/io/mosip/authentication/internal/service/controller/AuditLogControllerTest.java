package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.service.AuditLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AuditLogControllerTest {

    @InjectMocks
    private AuditLogController controller;

    @Mock
    private AuditLogService auditLogService;

    @Test
    public void testLogAuditEvent_ValidRequest_Returns201() {
        AuditLogRequestDto request = new AuditLogRequestDto("LOGIN", "User login", "user123");
        AuditLogResponseDto mockResponse = new AuditLogResponseDto("event-id-123", LocalDateTime.now());
        when(auditLogService.logEvent(any())).thenReturn(mockResponse);

        ResponseEntity<AuditLogResponseDto> response = controller.logAuditEvent(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("event-id-123", response.getBody().getEventId());
        verify(auditLogService).logEvent(request);
    }

    @Test
    public void testLogAuditEvent_ServiceCalled() {
        AuditLogRequestDto request = new AuditLogRequestDto("LOGOUT", "User logout", "user456");
        AuditLogResponseDto mockResponse = new AuditLogResponseDto("event-id-456", LocalDateTime.now());
        when(auditLogService.logEvent(any())).thenReturn(mockResponse);

        controller.logAuditEvent(request);

        verify(auditLogService, times(1)).logEvent(request);
    }

    @Test
    public void testLogAuditEvent_ResponseHasEventId() {
        AuditLogRequestDto request = new AuditLogRequestDto("ACCESS", "Resource accessed", "user789");
        AuditLogResponseDto mockResponse = new AuditLogResponseDto("event-id-789", LocalDateTime.now());
        when(auditLogService.logEvent(any())).thenReturn(mockResponse);

        ResponseEntity<AuditLogResponseDto> response = controller.logAuditEvent(request);

        assertNotNull(response.getBody().getEventId());
        assertNotNull(response.getBody().getTimestamp());
    }
}