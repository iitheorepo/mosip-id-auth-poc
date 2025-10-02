package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.internal.service.dto.HealthDetailsResponseDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class HealthDetailsControllerTest {

    @InjectMocks
    private HealthDetailsController controller;

    @Mock
    private EnvUtil envUtil;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(controller, "serviceEnabled", true);
        ReflectionTestUtils.setField(controller, "configurableProperty", "test-value");
    }

    @Test
    public void testGetHealthDetails_ServiceUp_ReturnsOk() {

        ResponseEntity<HealthDetailsResponseDto> response = controller.getHealthDetails();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().getStatus());
        assertEquals("test-value", response.getBody().getConfigurableProperty());
    }

    @Test
    public void testGetHealthDetails_ServiceDown_Returns503() {

        ReflectionTestUtils.setField(controller, "serviceEnabled", false);


        ResponseEntity<HealthDetailsResponseDto> response = controller.getHealthDetails();


        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("DOWN", response.getBody().getStatus());
        assertNull(response.getBody().getMetadata());
    }

    @Test
    public void testGetHealthDetails_MetadataPopulated() {

        ResponseEntity<HealthDetailsResponseDto> response = controller.getHealthDetails();


        assertNotNull(response.getBody().getMetadata());
        assertEquals("authentication-internal-service", response.getBody().getMetadata().get("serviceName"));
        assertNotNull(response.getBody().getMetadata().get("version"));
        assertNotNull(response.getBody().getMetadata().get("environment"));
    }

    @Test
    public void testGetHealthDetails_TimestampNotNull() {

        ResponseEntity<HealthDetailsResponseDto> response = controller.getHealthDetails();


        assertNotNull(response.getBody().getTimestamp());
    }
}