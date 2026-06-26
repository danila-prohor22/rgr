package com.sportsinventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_transaction_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private InventoryTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String notes;
}
