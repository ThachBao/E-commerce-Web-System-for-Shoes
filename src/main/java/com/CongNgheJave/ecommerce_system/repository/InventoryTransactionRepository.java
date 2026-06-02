package com.CongNgheJave.ecommerce_system.repository;

import com.CongNgheJave.ecommerce_system.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
}
