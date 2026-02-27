package com.blackpearl.controller;

import com.blackpearl.dto.PaymentDto;
import com.blackpearl.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentDto> getAll() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/my")
    public List<PaymentDto> getMine(Authentication auth) {
        return paymentService.getPaymentsByUser(auth.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> create(@RequestBody PaymentDto req, Authentication auth) {
        return ResponseEntity.ok(paymentService.createPayment(req, auth.getName()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> updateStatus(@PathVariable Long id,
            @RequestBody Map<String, String> req) {
        return ResponseEntity.ok(paymentService.updateStatus(id, req.get("status")));
    }
}
