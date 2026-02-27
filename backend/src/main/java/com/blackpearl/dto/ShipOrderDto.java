package com.blackpearl.dto;

import com.blackpearl.model.ShipOrder.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipOrderDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String shipType;
    private Integer tonnage;
    private String material;
    private String specifications;
    private LocalDate expectedDelivery;
    private Status status;
    private String adminNotes;
    private LocalDateTime createdAt;
}
