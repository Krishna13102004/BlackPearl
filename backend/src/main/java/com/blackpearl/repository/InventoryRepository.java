package com.blackpearl.repository;

import com.blackpearl.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByCategory(String category);

    List<Inventory> findByStatus(Inventory.Status status);

    boolean existsByItemCode(String itemCode);
}
