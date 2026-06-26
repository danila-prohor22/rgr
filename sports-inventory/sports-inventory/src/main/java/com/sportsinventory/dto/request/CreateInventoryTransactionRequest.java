package com.sportsinventory.dto.request;

import com.sportsinventory.entity.InventoryTransaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateInventoryTransactionRequest {

    @NotNull
    private Long storageLocationId;

    @NotNull
    private InventoryTransaction.TransactionType type;

    @NotBlank
    private String responsiblePerson;

    private String comment;

    @NotEmpty
    private List<TransactionItemRequest> items;

    @Data
    public static class TransactionItemRequest {
        @NotNull
        private Long equipmentId;

        @NotNull @Min(1)
        private Integer quantity;

        private String notes;
    }
}
