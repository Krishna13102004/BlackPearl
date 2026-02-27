package com.blackpearl.service;

import com.blackpearl.dto.StockExportDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.Inventory;
import com.blackpearl.model.StockExport;
import com.blackpearl.model.User;
import com.blackpearl.repository.InventoryRepository;
import com.blackpearl.repository.StockExportRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockExportService {

    private final StockExportRepository stockExportRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    public List<StockExportDto> getAllExports() {
        if (com.blackpearl.security.SecurityUtils.isAdmin()) {
            return stockExportRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return getExportsByUser(com.blackpearl.security.SecurityUtils.getCurrentUserEmail());
    }

    public List<StockExportDto> getExportsByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return stockExportRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockExportDto createExport(StockExportDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Inventory inventory = null;
        if (dto.getInventoryId() != null) {
            inventory = inventoryRepository.findById(dto.getInventoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found"));
        }

        StockExport export = StockExport.builder()
                .user(user)
                .inventory(inventory)
                .itemName(dto.getItemName())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .purpose(dto.getPurpose())
                .deliveryAddress(dto.getDeliveryAddress())
                .status(StockExport.Status.PENDING)
                .build();

        return convertToDto(stockExportRepository.save(export));
    }

    @Transactional
    public StockExportDto updateStatus(Long id, StockExport.Status status) {
        StockExport export = stockExportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock export not found with id: " + id));

        User admin = userRepository.findByEmail(com.blackpearl.security.SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (status == StockExport.Status.APPROVED && export.getStatus() != StockExport.Status.APPROVED) {
            if (export.getInventory() != null) {
                Inventory inv = export.getInventory();
                if (inv.getQuantity() < export.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient inventory quantity for " + inv.getName());
                }
                inv.setQuantity(inv.getQuantity() - export.getQuantity());
                if (inv.getQuantity() == 0)
                    inv.setStatus(Inventory.Status.OUT_OF_STOCK);
                else if (inv.getQuantity() < 10)
                    inv.setStatus(Inventory.Status.LOW_STOCK);
                inventoryRepository.save(inv);
            }
            export.setApprovedBy(admin);
            export.setApprovedAt(LocalDateTime.now());
        }

        export.setStatus(status);
        return convertToDto(stockExportRepository.save(export));
    }

    public StockExportDto convertToDto(StockExport e) {
        return StockExportDto.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .userEmail(e.getUser().getEmail())
                .inventoryId(e.getInventory() != null ? e.getInventory().getId() : null)
                .itemName(e.getItemName())
                .quantity(e.getQuantity())
                .unit(e.getUnit())
                .purpose(e.getPurpose())
                .deliveryAddress(e.getDeliveryAddress())
                .status(e.getStatus())
                .build();
    }
}
