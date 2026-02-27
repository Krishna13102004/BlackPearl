package com.blackpearl.service;

import com.blackpearl.dto.PaymentDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.Payment;
import com.blackpearl.model.User;
import com.blackpearl.repository.PaymentRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public List<PaymentDto> getAllPayments() {
        if (com.blackpearl.security.SecurityUtils.isAdmin()) {
            return paymentRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return getPaymentsByUser(com.blackpearl.security.SecurityUtils.getCurrentUserEmail());
    }

    public List<PaymentDto> getPaymentsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return paymentRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (!com.blackpearl.security.SecurityUtils.isAdmin() &&
                !payment.getUser().getEmail().equals(com.blackpearl.security.SecurityUtils.getCurrentUserEmail())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to view this payment");
        }

        return convertToDto(payment);
    }

    @Transactional
    public PaymentDto createPayment(PaymentDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Payment payment = Payment.builder()
                .user(user)
                .paymentRef(dto.getPaymentRef())
                .amount(dto.getAmount())
                .method(dto.getMethod() != null ? dto.getMethod() : Payment.Method.NEFT)
                .status(Payment.Status.PENDING)
                .description(dto.getDescription())
                .build();

        return convertToDto(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDto updateStatus(Long id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        User admin = userRepository.findByEmail(com.blackpearl.security.SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        Payment.Status newStatus = Payment.Status.valueOf(status.toUpperCase());
        if (newStatus == Payment.Status.COMPLETED && payment.getStatus() != Payment.Status.COMPLETED) {
            payment.setApprovedBy(admin);
            payment.setApprovedAt(LocalDateTime.now());
        }

        payment.setStatus(newStatus);
        return convertToDto(paymentRepository.save(payment));
    }

    public BigDecimal getMonthlyRevenue() {
        YearMonth current = YearMonth.now();
        LocalDateTime start = current.atDay(1).atStartOfDay();
        LocalDateTime end = current.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal revenue = paymentRepository.sumCompletedPaymentsBetween(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public PaymentDto convertToDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .userEmail(p.getUser().getEmail())
                .userName(p.getUser().getFullName())
                .paymentRef(p.getPaymentRef())
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
