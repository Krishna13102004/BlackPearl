package com.blackpearl.dto;

import com.blackpearl.model.Inventory.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    private Long id;
    private String itemCode;
    private String name;
    private String category;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private Status status;
}
