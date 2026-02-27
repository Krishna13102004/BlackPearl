package com.blackpearl.service;

import com.blackpearl.dto.ShipOrderDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.ShipOrder;
import com.blackpearl.model.User;
import com.blackpearl.repository.ShipOrderRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipOrderService {

    private final ShipOrderRepository shipOrderRepository;
    private final UserRepository userRepository;

    public List<ShipOrderDto> getAllOrders() {
        if (com.blackpearl.security.SecurityUtils.isAdmin()) {
            return shipOrderRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return getOrdersByUser(com.blackpearl.security.SecurityUtils.getCurrentUserEmail());
    }

    public List<ShipOrderDto> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return shipOrderRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipOrderDto getOrderById(Long id) {
        ShipOrder order = shipOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ship order not found with id: " + id));

        if (!com.blackpearl.security.SecurityUtils.isAdmin() &&
                !order.getUser().getEmail().equals(com.blackpearl.security.SecurityUtils.getCurrentUserEmail())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to view this order");
        }

        return convertToDto(order);
    }

    @Transactional
    public ShipOrderDto createOrder(ShipOrderDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        ShipOrder order = ShipOrder.builder()
                .user(user)
                .shipType(dto.getShipType())
                .tonnage(dto.getTonnage())
                .material(dto.getMaterial())
                .specifications(dto.getSpecifications())
                .expectedDelivery(dto.getExpectedDelivery())
                .status(ShipOrder.Status.PENDING)
                .build();

        return convertToDto(shipOrderRepository.save(order));
    }

    @Transactional
    public ShipOrderDto approveOrder(Long id) {
        ShipOrder order = shipOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ship order not found with id: " + id));

        User admin = userRepository.findByEmail(com.blackpearl.security.SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        order.setStatus(ShipOrder.Status.APPROVED);
        order.setApprovedBy(admin);
        order.setApprovedAt(java.time.LocalDateTime.now());

        return convertToDto(shipOrderRepository.save(order));
    }

    @Transactional
    public ShipOrderDto rejectOrder(Long id) {
        ShipOrder order = shipOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ship order not found with id: " + id));
        order.setStatus(ShipOrder.Status.REJECTED);
        return convertToDto(shipOrderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!shipOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ship order not found with id: " + id);
        }
        shipOrderRepository.deleteById(id);
    }

    public ShipOrderDto convertToDto(ShipOrder o) {
        return ShipOrderDto.builder()
                .id(o.getId())
                .userId(o.getUser().getId())
                .userEmail(o.getUser().getEmail())
                .shipType(o.getShipType())
                .tonnage(o.getTonnage())
                .material(o.getMaterial())
                .specifications(o.getSpecifications())
                .expectedDelivery(o.getExpectedDelivery())
                .status(o.getStatus())
                .adminNotes(o.getAdminNotes())
                .createdAt(o.getCreatedAt())
                .build();
    }
}
