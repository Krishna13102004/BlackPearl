package com.blackpearl.config;

import com.blackpearl.model.Inventory;
import com.blackpearl.model.Payment;
import com.blackpearl.model.Tender;
import com.blackpearl.model.User;
import com.blackpearl.repository.InventoryRepository;
import com.blackpearl.repository.PaymentRepository;
import com.blackpearl.repository.TenderRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final TenderRepository tenderRepository;
        private final InventoryRepository inventoryRepository;
        private final PaymentRepository paymentRepository;
        private final JdbcTemplate jdbcTemplate;

        @Override
        public void run(String... args) {
                // ── Normalize Existing Data ──────────────────────────────────────────────
                // Fix for Mixed-Case Departments (e.g., "Engineering" -> "ENGINEERING")
                try {
                        jdbcTemplate.execute(
                                        "UPDATE app_users SET department = UPPER(department) WHERE department IS NOT NULL");
                        log.info("✅ Database department values normalized to uppercase.");
                } catch (Exception e) {
                        log.warn("Could not normalize department names: {}", e.getMessage());
                }

                // ── Admin (ADMINISTRATION) ──────────────────────────────────────────────────
                if (userRepository.countByEmail("admin@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Admin").lastName("User")
                                        .email("admin@blackpearl.com")
                                        .password(passwordEncoder.encode("admin123"))
                                        .department(User.Department.ADMINISTRATION)
                                        .role(User.Role.ADMIN)
                                        .active(true).build());
                        log.info("✅ Admin seeded: admin@blackpearl.com / admin123");
                }

                // ── Engineering
                // ───────────────────────────────────────────────────────────────
                if (userRepository.countByEmail("eng@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Arjun").lastName("Engineer")
                                        .email("eng@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.ENGINEERING)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ Engineering user seeded: eng@blackpearl.com / user123");
                }

                // ── Operations
                // ────────────────────────────────────────────────────────────────
                if (userRepository.countByEmail("ops@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Karthik").lastName("Operations")
                                        .email("ops@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.OPERATIONS)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ Operations user seeded: ops@blackpearl.com / user123");
                }

                // ── Procurement
                // ───────────────────────────────────────────────────────────────
                if (userRepository.countByEmail("procure@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Deepa").lastName("Procurement")
                                        .email("procure@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.PROCUREMENT)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ Procurement user seeded: procure@blackpearl.com / user123");
                }

                // ── Finance ────────────────────────────────────────────────────────────────
                if (userRepository.countByEmail("finance@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Priya").lastName("Finance")
                                        .email("finance@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.FINANCE)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ Finance user seeded: finance@blackpearl.com / user123");
                }

                // ── QC (QUALITY_CONTROL) ────────────────────────────────────────────────────
                if (userRepository.countByEmail("qc@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Ravi").lastName("QC")
                                        .email("qc@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.QUALITY_CONTROL)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ QC user seeded: qc@blackpearl.com / user123");
                }

                // ── Safety ──────────────────────────────────────────────────────────────────
                if (userRepository.countByEmail("safety@blackpearl.com") == 0) {
                        userRepository.save(User.builder()
                                        .firstName("Suresh").lastName("Safety")
                                        .email("safety@blackpearl.com")
                                        .password(passwordEncoder.encode("user123"))
                                        .department(User.Department.SAFETY)
                                        .role(User.Role.USER)
                                        .active(true).build());
                        log.info("✅ Safety user seeded: safety@blackpearl.com / user123");
                }

                // ── Sample Tenders ─────────────────────────────────────────────────────────
                if (tenderRepository.count() == 0) {
                        tenderRepository.save(Tender.builder()
                                        .tenderNo("BPS-2026-001")
                                        .title("5000 DWT Cargo Vessel Construction")
                                        .description("Design and construction of a 5000 DWT cargo vessel including all marine systems.")
                                        .category("Ship Building")
                                        .value(new BigDecimal("125.50"))
                                        .publishedDate(LocalDate.now())
                                        .closingDate(LocalDate.now().plusMonths(2))
                                        .status(Tender.Status.OPEN)
                                        .build());

                        tenderRepository.save(Tender.builder()
                                        .tenderNo("BPS-2026-002")
                                        .title("Supply of Marine Grade Steel Plates")
                                        .description("Procurement of ASTM A131 Grade AH36 marine grade steel plates, 10-25mm thickness.")
                                        .category("Supply")
                                        .value(new BigDecimal("42.00"))
                                        .publishedDate(LocalDate.now())
                                        .closingDate(LocalDate.now().plusMonths(1))
                                        .status(Tender.Status.OPEN)
                                        .build());

                        tenderRepository.save(Tender.builder()
                                        .tenderNo("BPS-2026-003")
                                        .title("Dry Dock Repair – INS Defender")
                                        .description(
                                                        "Complete dry dock overhaul including hull inspection, blasting, painting, and propeller refurbishment.")
                                        .category("Ship Repair")
                                        .value(new BigDecimal("18.75"))
                                        .publishedDate(LocalDate.now())
                                        .closingDate(LocalDate.now().plusDays(20))
                                        .status(Tender.Status.CLOSING_SOON)
                                        .build());

                        log.info("✅ Sample tenders seeded.");
                }

                // ── Sample Inventory ───────────────────────────────────────────────────────
                if (inventoryRepository.count() == 0) {
                        inventoryRepository.save(Inventory.builder()
                                        .itemCode("INV-001")
                                        .name("Marine Engine MAN B&W 6S50ME")
                                        .category("Marine Engines")
                                        .quantity(3)
                                        .unit("Unit")
                                        .unitPrice(new BigDecimal("24000000"))
                                        .status(Inventory.Status.AVAILABLE)
                                        .build());

                        inventoryRepository.save(Inventory.builder()
                                        .itemCode("INV-002")
                                        .name("Marine Grade Steel Plate 10mm")
                                        .category("Steel & Metal")
                                        .quantity(500)
                                        .unit("Ton")
                                        .unitPrice(new BigDecimal("85000"))
                                        .status(Inventory.Status.AVAILABLE)
                                        .build());

                        inventoryRepository.save(Inventory.builder()
                                        .itemCode("INV-003")
                                        .name("Hydraulic Pump 250 Bar")
                                        .category("Hydraulics")
                                        .quantity(4)
                                        .unit("Unit")
                                        .unitPrice(new BigDecimal("620000"))
                                        .status(Inventory.Status.LOW_STOCK)
                                        .build());

                        inventoryRepository.save(Inventory.builder()
                                        .itemCode("INV-004")
                                        .name("Navigation Radar Furuno FR-2115")
                                        .category("Navigation")
                                        .quantity(0)
                                        .unit("Unit")
                                        .unitPrice(new BigDecimal("1800000"))
                                        .status(Inventory.Status.OUT_OF_STOCK)
                                        .build());

                        log.info("✅ Sample inventory seeded.");
                }

                // ── Sample Payments ────────────────────────────────────────────────────────
                if (paymentRepository.count() == 0) {
                        userRepository.findByEmail("eng@blackpearl.com").ifPresent(user -> {
                                paymentRepository.save(Payment.builder()
                                                .user(user)
                                                .paymentRef("PAY-2026-001")
                                                .amount(new BigDecimal("12500000"))
                                                .method(Payment.Method.RTGS)
                                                .status(Payment.Status.COMPLETED)
                                                .description("Advance payment for Ship Order SO-001")
                                                .build());

                                paymentRepository.save(Payment.builder()
                                                .user(user)
                                                .paymentRef("PAY-2026-002")
                                                .amount(new BigDecimal("4200000"))
                                                .method(Payment.Method.NEFT)
                                                .status(Payment.Status.PENDING)
                                                .description("Material procurement – Steel Plates")
                                                .build());

                                log.info("✅ Sample payments seeded.");
                        });
                }

                log.info("📋 Data initialization complete.");
        }
}
