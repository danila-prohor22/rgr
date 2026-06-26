package com.sportsinventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_locations")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String address;

    @Column(name = "sport_type", length = 80)
    private String sportType;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "storageLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Equipment> equipment = new ArrayList<>();

    @OneToMany(mappedBy = "storageLocation", fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryTransaction> transactions = new ArrayList<>();
}
