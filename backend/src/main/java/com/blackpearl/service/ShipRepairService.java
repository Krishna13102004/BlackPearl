package com.blackpearl.service;

import com.blackpearl.dto.ShipRepairDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.ShipRepair;
import com.blackpearl.model.User;
import com.blackpearl.repository.ShipRepairRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipRepairService {

    private final ShipRepairRepository shipRepairRepository;
    private final UserRepository userRepository;

    public List<ShipRepairDto> getAllRepairs() {
        return shipRepairRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ShipRepairDto> getRepairsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return shipRepairRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipRepairDto createRepair(ShipRepairDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        ShipRepair repair = ShipRepair.builder()
                .user(user)
                .vesselName(dto.getVesselName())
                .issueType(dto.getIssueType())
                .description(dto.getDescription())
                .priority(dto.getPriority() != null ? dto.getPriority() : ShipRepair.Priority.MEDIUM)
                .status(ShipRepair.Status.PENDING)
                .build();

        return convertToDto(shipRepairRepository.save(repair));
    }

    @Transactional
    public ShipRepairDto updateStatus(Long id, ShipRepair.Status status, String notes) {
        ShipRepair repair = shipRepairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ship repair not found with id: " + id));
        repair.setStatus(status);
        if (notes != null)
            repair.setTechnicianNotes(notes);
        return convertToDto(shipRepairRepository.save(repair));
    }

    public ShipRepairDto convertToDto(ShipRepair r) {
        return ShipRepairDto.builder()
                .id(r.getId())
                .userId(r.getUser().getId())
                .userEmail(r.getUser().getEmail())
                .vesselName(r.getVesselName())
                .issueType(r.getIssueType())
                .description(r.getDescription())
                .priority(r.getPriority())
                .status(r.getStatus())
                .technicianNotes(r.getTechnicianNotes())
                .build();
    }
}
