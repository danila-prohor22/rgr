package com.sportsinventory.dto.response;

import com.sportsinventory.entity.InventoryTransaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class InventoryTransactionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long storageLocationId;
    private String storageLocationName;
    private InventoryTransaction.TransactionType type;
    private InventoryTransaction.TransactionStatus status;
    private String responsiblePerson;
    private Integer totalQuantity;
    private String comment;
    private LocalDateTime transactionDate;
    private List<TransactionItemResponse> items;

    @Data @Builder
    public static class TransactionItemResponse {
        private Long id;
        private Long equipmentId;
        private String equipmentName;
        private String inventoryCode;
        private Integer quantity;
        private String notes;
    }
}
