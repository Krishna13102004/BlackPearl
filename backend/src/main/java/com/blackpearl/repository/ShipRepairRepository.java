package com.blackpearl.repository;

import com.blackpearl.model.ShipRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepairRepository extends JpaRepository<ShipRepair, Long> {
    List<ShipRepair> findByUserId(Long userId);

    List<ShipRepair> findByStatus(ShipRepair.Status status);
}
