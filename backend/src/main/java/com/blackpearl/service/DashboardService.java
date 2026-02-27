package com.blackpearl.service;

import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.ShipOrder;
import com.blackpearl.model.Tender;
import com.blackpearl.model.User;
import com.blackpearl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ShipOrderRepository shipOrderRepository;
    private final ShipRepairRepository shipRepairRepository;
    private final TenderRepository tenderRepository;
    private final StockExportRepository stockExportRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;

    public Map<String, Object> getUserStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        Long uid = user.getId();

        return Map.of(
                "shipOrders", shipOrderRepository.findByUserId(uid).size(),
                "repairs", shipRepairRepository.findByUserId(uid).size(),
                "tenders", tenderRepository.findByStatus(Tender.Status.OPEN).size(),
                "stockExports", stockExportRepository.findByUserId(uid).size());
    }

    public Map<String, Object> getAdminStats() {
        YearMonth current = YearMonth.now();
        LocalDateTime start = current.atDay(1).atStartOfDay();
        LocalDateTime end = current.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal monthRevenue = paymentRepository.sumCompletedPaymentsBetween(start, end);
        if (monthRevenue == null) monthRevenue = BigDecimal.ZERO;

        return Map.of(
                "totalUsers", userRepository.count(),
                "totalOrders", shipOrderRepository.count(),
                "activeTenders", tenderRepository.findByStatus(Tender.Status.OPEN).size(),
                "totalInventory", inventoryRepository.count(),
                "monthRevenue", monthRevenue);
    }

    /**
     * Full dashboard summary: totalUsers, totalOrders, activeTenders, totalRevenue,
     * revenueByMonth (last 6 months), ordersByStatus (for donut chart).
     */
    public Map<String, Object> getDashboardSummary() {
        long totalUsers = userRepository.count();
        long totalOrders = shipOrderRepository.count();
        long activeTenders = tenderRepository.findByStatus(Tender.Status.OPEN).size();

        BigDecimal totalRevenue = paymentRepository.sumTotalCompletedPayments();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // Revenue by month (last 6 months)
        List<Map<String, Object>> revenueByMonth = new ArrayList<>();
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            BigDecimal rev = paymentRepository.sumCompletedPaymentsBetween(
                    ym.atDay(1).atStartOfDay(),
                    ym.atEndOfMonth().atTime(23, 59, 59));
            if (rev == null) rev = BigDecimal.ZERO;
            revenueByMonth.add(Map.of(
                    "month", monthNames[ym.getMonthValue() - 1],
                    "year", ym.getYear(),
                    "revenue", rev.doubleValue()));
        }

        // Orders by status (for donut chart)
        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        for (ShipOrder.Status s : ShipOrder.Status.values()) {
            ordersByStatus.put(s.name(), (long) shipOrderRepository.findByStatus(s).size());
        }

        return Map.of(
                "totalUsers", totalUsers,
                "totalOrders", totalOrders,
                "activeTenders", activeTenders,
                "totalRevenue", totalRevenue.doubleValue(),
                "revenueByMonth", revenueByMonth,
                "ordersByStatus", ordersByStatus);
    }
}
