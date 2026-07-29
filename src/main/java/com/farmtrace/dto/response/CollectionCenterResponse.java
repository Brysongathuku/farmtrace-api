package com.farmtrace.dto.response;

import com.farmtrace.model.CollectionCenter;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CollectionCenterResponse {
    private UUID id;
    private String name;
    private String location;
    private UUID cooperativeId;
    private String cooperativeName;
    private LocalDateTime createdAt;

    public static CollectionCenterResponse from(CollectionCenter center) {
        return CollectionCenterResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .location(center.getLocation())
                .cooperativeId(center.getCooperative().getId())
                .cooperativeName(center.getCooperative().getName())
                .createdAt(center.getCreatedAt())
                .build();
    }
}
