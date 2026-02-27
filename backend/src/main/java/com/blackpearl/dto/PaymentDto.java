package com.blackpearl.dto;

import com.blackpearl.model.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private String paymentRef;
    private BigDecimal amount;
    private Payment.Method method;
    private Payment.Status status;
    private String description;
    private LocalDateTime createdAt;
}
