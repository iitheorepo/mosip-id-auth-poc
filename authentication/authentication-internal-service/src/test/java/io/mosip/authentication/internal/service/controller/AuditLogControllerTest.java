package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import io.mosip.authentication.internal.service.service.AuditLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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

    @Test
    public void testGetAuditEvents_NoFilters_ReturnsAllEvents() {
        List<AuditLogEntity> mockEvents = Arrays.asList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now()),
            new AuditLogEntity("event2", EventType.LOGOUT, "Logout event", "user456", LocalDateTime.now())
        );
        when(auditLogService.getEvents(isNull(), isNull(), eq("timestamp"), eq("desc"))).thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents(null, null, "timestamp", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(auditLogService).getEvents(null, null, "timestamp", "desc");
    }

    @Test
    public void testGetAuditEvents_FilterByUserId_ReturnsFilteredEvents() {
        List<AuditLogEntity> mockEvents = Arrays.asList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now()),
            new AuditLogEntity("event2", EventType.ACCESS, "Access event", "user123", LocalDateTime.now())
        );
        when(auditLogService.getEvents(eq("user123"), isNull(), eq("timestamp"), eq("desc"))).thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents("user123", null, "timestamp", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("user123", response.getBody().get(0).getUserId());
        verify(auditLogService).getEvents("user123", null, "timestamp", "desc");
    }

    @Test
    public void testGetAuditEvents_FilterByEventType_ReturnsFilteredEvents() {
        List<AuditLogEntity> mockEvents = Collections.singletonList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now())
        );
        when(auditLogService.getEvents(isNull(), eq(EventType.LOGIN), eq("timestamp"), eq("desc"))).thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents(null, EventType.LOGIN, "timestamp", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(EventType.LOGIN, response.getBody().get(0).getEventType());
        verify(auditLogService).getEvents(null, EventType.LOGIN, "timestamp", "desc");
    }

    @Test
    public void testGetAuditEvents_FilterByUserIdAndEventType_ReturnsFilteredEvents() {
        List<AuditLogEntity> mockEvents = Collections.singletonList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now())
        );
        when(auditLogService.getEvents(eq("user123"), eq(EventType.LOGIN), eq("timestamp"), eq("desc")))
            .thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents("user123", EventType.LOGIN, "timestamp", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("user123", response.getBody().get(0).getUserId());
        assertEquals(EventType.LOGIN, response.getBody().get(0).getEventType());
        verify(auditLogService).getEvents("user123", EventType.LOGIN, "timestamp", "desc");
    }

    @Test
    public void testGetAuditEvents_CustomSortByUserId_ReturnsEvents() {
        List<AuditLogEntity> mockEvents = Arrays.asList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now()),
            new AuditLogEntity("event2", EventType.LOGOUT, "Logout event", "user456", LocalDateTime.now())
        );
        when(auditLogService.getEvents(isNull(), isNull(), eq("userId"), eq("asc"))).thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents(null, null, "userId", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(auditLogService).getEvents(null, null, "userId", "asc");
    }

    @Test
    public void testGetAuditEvents_SortOrderAscending_ReturnsEvents() {
        List<AuditLogEntity> mockEvents = Collections.singletonList(
            new AuditLogEntity("event1", EventType.LOGIN, "Login event", "user123", LocalDateTime.now())
        );
        when(auditLogService.getEvents(isNull(), isNull(), eq("timestamp"), eq("asc"))).thenReturn(mockEvents);

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents(null, null, "timestamp", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditLogService).getEvents(null, null, "timestamp", "asc");
    }

    @Test
    public void testGetAuditEvents_EmptyResult_ReturnsEmptyList() {
        when(auditLogService.getEvents(eq("nonexistent"), isNull(), eq("timestamp"), eq("desc")))
            .thenReturn(Collections.emptyList());

        ResponseEntity<List<AuditLogEntity>> response = controller.getAuditEvents("nonexistent", null, "timestamp", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(auditLogService).getEvents("nonexistent", null, "timestamp", "desc");
    }
}