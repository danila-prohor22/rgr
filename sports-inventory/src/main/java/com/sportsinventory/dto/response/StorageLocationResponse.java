package com.sportsinventory.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class StorageLocationResponse {
    private Long id;
    private String name;
    private String address;
    private String sportType;
    private Integer capacity;
    private Boolean active;
}
