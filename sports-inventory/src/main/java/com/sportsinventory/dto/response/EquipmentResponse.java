package com.sportsinventory.dto.response;

import com.sportsinventory.entity.Equipment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class EquipmentResponse {
    private Long id;
    private Long storageLocationId;
    private String storageLocationName;
    private String name;
    private String inventoryCode;
    private String description;
    private String category;
    private Equipment.ConditionStatus conditionStatus;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private BigDecimal replacementCost;
    private Boolean active;
}
