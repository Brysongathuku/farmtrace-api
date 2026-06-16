package com.farmtrace.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCooperativeRequest {
    @NotBlank(message = "Cooperative name is required")
    private String name;

    private String region;
}
