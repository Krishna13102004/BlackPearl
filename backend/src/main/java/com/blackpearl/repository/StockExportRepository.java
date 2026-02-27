package com.blackpearl.repository;

import com.blackpearl.model.StockExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockExportRepository extends JpaRepository<StockExport, Long> {
    List<StockExport> findByUserId(Long userId);

    List<StockExport> findByStatus(StockExport.Status status);
}
