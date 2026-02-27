package com.blackpearl.controller;

import com.blackpearl.dto.InventoryDto;
import com.blackpearl.dto.TenderDto;
import com.blackpearl.service.InventoryService;
import com.blackpearl.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"}, allowCredentials = "true")
@RequiredArgsConstructor
public class PublicController {

    private final TenderService tenderService;
    private final InventoryService inventoryService;

    /**
     * Public endpoint – no authentication required.
     * Returns open tenders for display on the landing page.
     */
    @GetMapping("/tenders")
    public List<TenderDto> getOpenTenders() {
        return tenderService.getOpenTenders();
    }

    /**
     * Public endpoint – no authentication required.
     * Returns available inventory items for display on the landing page.
     */
    @GetMapping("/inventory")
    public List<InventoryDto> getAvailableInventory() {
        return inventoryService.getAvailableItems();
    }
}
