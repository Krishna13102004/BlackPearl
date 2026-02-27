package com.blackpearl.controller;

import com.blackpearl.dto.StockExportDto;
import com.blackpearl.model.StockExport;
import com.blackpearl.service.StockExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-exports")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('OPERATIONS', 'ADMIN')")
public class StockExportController {

    private final StockExportService stockExportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockExportDto> getAll() {
        return stockExportService.getAllExports();
    }

    @GetMapping("/my")
    public List<StockExportDto> getMine(Authentication auth) {
        return stockExportService.getExportsByUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<StockExportDto> create(@RequestBody StockExportDto req, Authentication auth) {
        return ResponseEntity.ok(stockExportService.createExport(req, auth.getName()));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockExportDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(stockExportService.updateStatus(id, StockExport.Status.APPROVED));
    }

    @PatchMapping("/{id}/dispatch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockExportDto> dispatch(@PathVariable Long id) {
        return ResponseEntity.ok(stockExportService.updateStatus(id, StockExport.Status.DISPATCHED));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockExportDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(stockExportService.updateStatus(id, StockExport.Status.REJECTED));
    }
}
