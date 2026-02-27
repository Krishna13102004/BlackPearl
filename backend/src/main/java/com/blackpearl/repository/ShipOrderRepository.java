package com.blackpearl.repository;

import com.blackpearl.model.ShipOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipOrderRepository extends JpaRepository<ShipOrder, Long> {
    List<ShipOrder> findByUserId(Long userId);

    List<ShipOrder> findByStatus(ShipOrder.Status status);
}
