package com.blackpearl.dto;

import com.blackpearl.model.ShipRepair.Priority;
import com.blackpearl.model.ShipRepair.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipRepairDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String vesselName;
    private String issueType;
    private String description;
    private Priority priority;
    private Status status;
    private String technicianNotes;
}
