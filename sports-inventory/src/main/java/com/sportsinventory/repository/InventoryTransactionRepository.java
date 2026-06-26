package com.sportsinventory.repository;

import com.sportsinventory.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    Page<InventoryTransaction> findByUserEmail(String email, Pageable pageable);
    Optional<InventoryTransaction> findByUserEmailAndId(String email, Long id);
    List<InventoryTransaction> findByStatus(InventoryTransaction.TransactionStatus status);
    Page<InventoryTransaction> findByStorageLocationId(Long storageLocationId, Pageable pageable);
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
}
