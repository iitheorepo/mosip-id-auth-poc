package io.mosip.authentication.internal.service.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequestDto {

    @NotBlank(message = "Event type is mandatory")
    private String eventType;

    private String description;

    @NotBlank(message = "User ID is mandatory")
    private String userId;
}