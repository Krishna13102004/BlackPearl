package com.blackpearl.controller;

import com.blackpearl.dto.ShipOrderDto;
import com.blackpearl.service.ShipOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ship-orders")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ENGINEERING', 'ADMIN')")
public class ShipOrderController {

    private final ShipOrderService shipOrderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShipOrderDto> getAll() {
        return shipOrderService.getAllOrders();
    }

    @GetMapping("/my")
    public List<ShipOrderDto> getMine(Authentication auth) {
        return shipOrderService.getOrdersByUser(auth.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipOrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shipOrderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<ShipOrderDto> create(@RequestBody ShipOrderDto req, Authentication auth) {
        return ResponseEntity.ok(shipOrderService.createOrder(req, auth.getName()));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipOrderDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(shipOrderService.approveOrder(id));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipOrderDto> reject(@PathVariable Long id) {
        return ResponseEntity.ok(shipOrderService.rejectOrder(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shipOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
