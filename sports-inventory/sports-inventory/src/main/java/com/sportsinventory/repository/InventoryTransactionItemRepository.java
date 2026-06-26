package com.sportsinventory.repository;

import com.sportsinventory.entity.InventoryTransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionItemRepository extends JpaRepository<InventoryTransactionItem, Long> {
}
