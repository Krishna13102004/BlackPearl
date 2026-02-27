package com.blackpearl.controller;

import com.blackpearl.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userStats(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getUserStats(auth.getName()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }

    /**
     * Full dashboard summary: totalUsers, totalOrders, activeTenders, totalRevenue,
     * revenueByMonth, ordersByStatus for charts.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> summary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
