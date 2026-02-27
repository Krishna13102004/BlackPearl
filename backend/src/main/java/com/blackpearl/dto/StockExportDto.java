package com.blackpearl.dto;

import com.blackpearl.model.StockExport.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockExportDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long inventoryId;
    private String itemName;
    private Integer quantity;
    private String unit;
    private String purpose;
    private String deliveryAddress;
    private Status status;
}
