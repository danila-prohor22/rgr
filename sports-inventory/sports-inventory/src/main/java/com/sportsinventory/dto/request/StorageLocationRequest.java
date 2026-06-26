package com.sportsinventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StorageLocationRequest {
    @NotBlank
    private String name;

    private String address;
    private String sportType;

    @Min(0)
    private Integer capacity = 0;

    private Boolean active = true;
}
