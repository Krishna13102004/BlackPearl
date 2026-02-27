package com.blackpearl.dto;

import com.blackpearl.model.Tender.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenderDto {
    private Long id;
    private String tenderNo;
    private String title;
    private String description;
    private String category;
    private BigDecimal value;
    private LocalDate publishedDate;
    private LocalDate closingDate;
    private Status status;
}
