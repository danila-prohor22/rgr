package com.sportsinventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_location_id", nullable = false)
    private StorageLocation storageLocation;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "inventory_code", nullable = false, unique = true, length = 80)
    private String inventoryCode;

    @Column(length = 500)
    private String description;

    @Column(length = 80)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", nullable = false, length = 30)
    @Builder.Default
    private ConditionStatus conditionStatus = ConditionStatus.GOOD;

    @Column(name = "quantity_total", nullable = false)
    @Builder.Default
    private Integer quantityTotal = 0;

    @Column(name = "quantity_available", nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Column(name = "replacement_cost", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal replacementCost = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryTransactionItem> transactionItems = new ArrayList<>();

    public enum ConditionStatus {
        NEW, GOOD, NEEDS_REPAIR, BROKEN, WRITTEN_OFF
    }
}
