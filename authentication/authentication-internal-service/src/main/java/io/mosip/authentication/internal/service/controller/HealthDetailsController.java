package io.mosip.authentication.internal.service.controller;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.internal.service.dto.HealthDetailsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "health-details-controller", description = "Health Details Controller")
public class HealthDetailsController {

    @Autowired
    private EnvUtil envUtil;

    @Value("${mosip.ida.health.service.enabled:true}")
    private boolean serviceEnabled;

    @Value("${mosip.ida.configurable.property:default-config-value}")
    private String configurableProperty;

    /**
     * Get health details of the service
     *
     * @return ResponseEntity with HealthDetailsResponseDto
     */
    @GetMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Health Details",
               description = "Returns service health status and metadata")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is UP"),
        @ApiResponse(responseCode = "503", description = "Service is DOWN")
    })
    public ResponseEntity<HealthDetailsResponseDto> getHealthDetails() {

        LocalDateTime timestamp = LocalDateTime.now();

        if (!serviceEnabled) {
            HealthDetailsResponseDto response = new HealthDetailsResponseDto(
                "DOWN",
                timestamp,
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("serviceName", "authentication-internal-service");
        metadata.put("version", getVersion());
        metadata.put("environment", getEnvironment());

        HealthDetailsResponseDto response = new HealthDetailsResponseDto(
            "UP",
            timestamp,
            metadata,
            configurableProperty
        );

        return ResponseEntity.ok(response);
    }


    private String getVersion() {
        return System.getProperty("application.version", "1.2.1.0");
    }


    private String getEnvironment() {
        return System.getProperty("mosip.environment", "development");
    }
}