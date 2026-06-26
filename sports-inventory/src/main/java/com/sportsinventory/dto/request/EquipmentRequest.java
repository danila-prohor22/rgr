package com.sportsinventory.dto.request;

import com.sportsinventory.entity.Equipment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EquipmentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String inventoryCode;

    private String description;
    private String category;

    private Equipment.ConditionStatus conditionStatus = Equipment.ConditionStatus.GOOD;

    @NotNull @Min(0)
    private Integer quantityTotal;

    @NotNull @Min(0)
    private Integer quantityAvailable;

    @NotNull @DecimalMin("0.00")
    private BigDecimal replacementCost;

    private Boolean active = true;
}
