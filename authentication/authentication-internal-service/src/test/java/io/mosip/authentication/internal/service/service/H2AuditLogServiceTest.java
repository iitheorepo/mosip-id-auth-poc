package io.mosip.authentication.internal.service.service;

import io.mosip.authentication.internal.service.dto.AuditLogRequestDto;
import io.mosip.authentication.internal.service.dto.AuditLogResponseDto;
import io.mosip.authentication.internal.service.entity.AuditLogEntity;
import io.mosip.authentication.internal.service.dto.EventType;
import io.mosip.authentication.internal.service.repository.AuditLogRepository;
import io.mosip.authentication.internal.service.service.impl.H2AuditLogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class H2AuditLogServiceTest {

    @InjectMocks
    private H2AuditLogService service;

    @Mock
    private AuditLogRepository repository;

    @Before
    public void setUp() {
        // Setup is handled by Mockito annotations
    }

    @Test
    public void testLogEvent_ValidRequest_Success() {

        AuditLogRequestDto request = new AuditLogRequestDto("LOGIN", "User login", "user123");
        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));


        AuditLogResponseDto response = service.logEvent(request);


        assertNotNull(response);
        assertNotNull(response.getEventId());
        assertNotNull(response.getTimestamp());
        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }

    @Test
    public void testLogEvent_GeneratesUniqueEventIds() {

        AuditLogRequestDto request1 = new AuditLogRequestDto("LOGIN", "Login 1", "user1");
        AuditLogRequestDto request2 = new AuditLogRequestDto("LOGOUT", "Logout 1", "user2");
        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));


        AuditLogResponseDto response1 = service.logEvent(request1);
        AuditLogResponseDto response2 = service.logEvent(request2);

        assertNotEquals(response1.getEventId(), response2.getEventId());
    }

    @Test
    public void testGetAllEvents_ReturnsStoredEvents() {

        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        AuditLogEntity entity2 = new AuditLogEntity("id2", EventType.LOGOUT, "desc2", "user2", LocalDateTime.now());
        when(repository.findAll()).thenReturn(Arrays.asList(entity1, entity2));


        List<AuditLogEntity> events = service.getAllEvents();

        assertEquals(2, events.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testLogEvent_WithOptionalDescription() {

        AuditLogRequestDto request = new AuditLogRequestDto("ACCESS", null, "user789");
        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuditLogResponseDto response = service.logEvent(request);

        assertNotNull(response);
        assertNotNull(response.getEventId());
    }

    @Test
    public void testGetAllEvents_ReturnsEmptyListInitially() {

        when(repository.findAll()).thenReturn(Arrays.asList());

        List<AuditLogEntity> events = service.getAllEvents();

        assertTrue(events.isEmpty());
    }

    @Test
    public void testLogEvent_StoresAllFields() {

        AuditLogRequestDto request = new AuditLogRequestDto("LOGIN", "Test description", "user123");
        when(repository.save(any(AuditLogEntity.class))).thenAnswer(invocation -> {
            AuditLogEntity savedEntity = invocation.getArgument(0);
            assertEquals(EventType.LOGIN, savedEntity.getEventType());
            assertEquals("Test description", savedEntity.getDescription());
            assertEquals("user123", savedEntity.getUserId());
            assertNotNull(savedEntity.getEventId());
            assertNotNull(savedEntity.getTimestamp());
            return savedEntity;
        });

        service.logEvent(request);

        verify(repository, times(1)).save(any(AuditLogEntity.class));
    }

    @Test
    public void testGetEvents_NoFilters_ReturnsAllEventsSorted() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        AuditLogEntity entity2 = new AuditLogEntity("id2", EventType.LOGOUT, "desc2", "user2", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findAll(any(Sort.class))).thenReturn(Arrays.asList(entity1, entity2));

        List<AuditLogEntity> events = service.getEvents(null, null, "timestamp", "desc");

        assertEquals(2, events.size());
        verify(repository).findAll(sort);
    }

    @Test
    public void testGetEvents_FilterByUserId_ReturnsFilteredEvents() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user123", LocalDateTime.now());
        AuditLogEntity entity2 = new AuditLogEntity("id2", EventType.ACCESS, "desc2", "user123", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findByUserId(eq("user123"), any(Sort.class))).thenReturn(Arrays.asList(entity1, entity2));

        List<AuditLogEntity> events = service.getEvents("user123", null, "timestamp", "desc");

        assertEquals(2, events.size());
        assertEquals("user123", events.get(0).getUserId());
        verify(repository).findByUserId(eq("user123"), eq(sort));
    }

    @Test
    public void testGetEvents_FilterByEventType_ReturnsFilteredEvents() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findByEventType(eq(EventType.LOGIN), any(Sort.class))).thenReturn(Collections.singletonList(entity1));

        List<AuditLogEntity> events = service.getEvents(null, EventType.LOGIN, "timestamp", "desc");

        assertEquals(1, events.size());
        assertEquals(EventType.LOGIN, events.get(0).getEventType());
        verify(repository).findByEventType(eq(EventType.LOGIN), eq(sort));
    }

    @Test
    public void testGetEvents_FilterByUserIdAndEventType_ReturnsFilteredEvents() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user123", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findByUserIdAndEventType(eq("user123"), eq(EventType.LOGIN), any(Sort.class)))
            .thenReturn(Collections.singletonList(entity1));

        List<AuditLogEntity> events = service.getEvents("user123", EventType.LOGIN, "timestamp", "desc");

        assertEquals(1, events.size());
        assertEquals("user123", events.get(0).getUserId());
        assertEquals(EventType.LOGIN, events.get(0).getEventType());
        verify(repository).findByUserIdAndEventType(eq("user123"), eq(EventType.LOGIN), eq(sort));
    }

    @Test
    public void testGetEvents_SortAscending_AppliesCorrectSort() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        when(repository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(entity1));

        List<AuditLogEntity> events = service.getEvents(null, null, "timestamp", "asc");

        assertEquals(1, events.size());
        verify(repository).findAll(eq(sort));
    }

    @Test
    public void testGetEvents_CustomSortField_AppliesCorrectSort() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.ASC, "userId");
        when(repository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(entity1));

        List<AuditLogEntity> events = service.getEvents(null, null, "userId", "asc");

        assertEquals(1, events.size());
        verify(repository).findAll(eq(sort));
    }

    @Test
    public void testGetEvents_DefaultSortField_UsesTimestamp() {
        AuditLogEntity entity1 = new AuditLogEntity("id1", EventType.LOGIN, "desc1", "user1", LocalDateTime.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(entity1));

        List<AuditLogEntity> events = service.getEvents(null, null, null, "desc");

        assertEquals(1, events.size());
        verify(repository).findAll(eq(sort));
    }

    @Test
    public void testGetEvents_EmptyResult_ReturnsEmptyList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        when(repository.findByUserId(eq("nonexistent"), any(Sort.class))).thenReturn(Collections.emptyList());

        List<AuditLogEntity> events = service.getEvents("nonexistent", null, "timestamp", "desc");

        assertTrue(events.isEmpty());
        verify(repository).findByUserId(eq("nonexistent"), eq(sort));
    }
}
