package com.sportsinventory.repository;

import com.sportsinventory.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Page<Equipment> findByActiveTrue(Pageable pageable);
    List<Equipment> findByCategoryIgnoreCaseAndActiveTrue(String category);
    List<Equipment> findByStorageLocationIdAndActiveTrue(Long storageLocationId);
    List<Equipment> findByStorageLocationIdAndCategoryIgnoreCaseAndActiveTrue(Long storageLocationId, String category);
    List<Equipment> findByQuantityAvailableGreaterThanAndActiveTrue(Integer quantityAvailable);
    Optional<Equipment> findByInventoryCode(String inventoryCode);
    boolean existsByInventoryCode(String inventoryCode);
}
