package com.farmtrace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCollectionCenterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String location;

    @NotNull(message = "Cooperative is required")
    private UUID cooperativeId;
}
