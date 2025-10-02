package io.mosip.authentication.internal.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDetailsResponseDto {
    private String status;                    // UP or DOWN
    private LocalDateTime timestamp;
    private Map<String, String> metadata;     // serviceName, version, environment
    private String configurableProperty;
}