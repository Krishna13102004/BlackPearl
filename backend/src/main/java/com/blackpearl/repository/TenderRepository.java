package com.blackpearl.repository;

import com.blackpearl.model.Tender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {
    List<Tender> findByStatus(Tender.Status status);

    boolean existsByTenderNo(String tenderNo);
}
