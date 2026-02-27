package com.blackpearl.controller;

import com.blackpearl.dto.ShipRepairDto;
import com.blackpearl.model.ShipRepair;
import com.blackpearl.service.ShipRepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ship-repairs")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ENGINEERING', 'ADMIN')")
public class ShipRepairController {

    private final ShipRepairService shipRepairService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShipRepairDto> getAll() {
        return shipRepairService.getAllRepairs();
    }

    @GetMapping("/my")
    public List<ShipRepairDto> getMine(Authentication auth) {
        return shipRepairService.getRepairsByUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<ShipRepairDto> create(@RequestBody ShipRepairDto req, Authentication auth) {
        return ResponseEntity.ok(shipRepairService.createRepair(req, auth.getName()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipRepairDto> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> req) {
        ShipRepair.Status status = ShipRepair.Status.valueOf(req.get("status"));
        String notes = req.get("notes");
        return ResponseEntity.ok(shipRepairService.updateStatus(id, status, notes));
    }
}
