package com.blackpearl.service;

import com.blackpearl.dto.InventoryDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.Inventory;
import com.blackpearl.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public InventoryDto getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
    }

    @Transactional
    public InventoryDto createItem(InventoryDto dto) {
        Inventory item = Inventory.builder()
                .itemCode(dto.getItemCode())
                .name(dto.getName())
                .category(dto.getCategory())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 0)
                .unit(dto.getUnit())
                .unitPrice(dto.getUnitPrice())
                .status(Inventory.Status.AVAILABLE)
                .build();
        recalculateStatus(item);
        return convertToDto(inventoryRepository.save(item));
    }

    @Transactional
    public InventoryDto updateItem(Long id, InventoryDto dto) {
        Inventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory());
        if (dto.getQuantity() != null) {
            item.setQuantity(dto.getQuantity());
            recalculateStatus(item);
        }
        if (dto.getUnit() != null) item.setUnit(dto.getUnit());
        if (dto.getUnitPrice() != null) item.setUnitPrice(dto.getUnitPrice());
        return convertToDto(inventoryRepository.save(item));
    }

    private void recalculateStatus(Inventory item) {
        int qty = item.getQuantity() != null ? item.getQuantity() : 0;
        if (qty <= 0) item.setStatus(Inventory.Status.OUT_OF_STOCK);
        else if (qty <= 5) item.setStatus(Inventory.Status.LOW_STOCK);
        else item.setStatus(Inventory.Status.AVAILABLE);
    }

    @Transactional
    public InventoryDto restock(Long id, Integer quantity) {
        Inventory item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
        item.setQuantity((item.getQuantity() != null ? item.getQuantity() : 0) + quantity);
        recalculateStatus(item);
        return convertToDto(inventoryRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory item not found with id: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    public List<InventoryDto> getAvailableItems() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getStatus() == Inventory.Status.AVAILABLE)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public InventoryDto convertToDto(Inventory i) {
        return InventoryDto.builder()
                .id(i.getId())
                .itemCode(i.getItemCode())
                .name(i.getName())
                .category(i.getCategory())
                .quantity(i.getQuantity())
                .unit(i.getUnit())
                .unitPrice(i.getUnitPrice())
                .status(i.getStatus())
                .build();
    }
}
