package com.blackpearl.controller;

import com.blackpearl.dto.TenderDto;
import com.blackpearl.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tenders")
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:3000" }, allowCredentials = "true")
@RequiredArgsConstructor
public class TenderController {

    private final TenderService tenderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TenderDto> getAll() {
        return tenderService.getAllTenders();
    }

    @GetMapping("/open")
    @org.springframework.security.access.prepost.PreAuthorize("permitAll()")
    public List<TenderDto> getOpen() {
        return tenderService.getOpenTenders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tenderService.getTenderById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenderDto> create(@RequestBody TenderDto req) {
        return ResponseEntity.ok(tenderService.createTender(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenderDto> update(@PathVariable Long id, @RequestBody TenderDto req) {
        req.setId(id);
        return ResponseEntity.ok(tenderService.updateTender(req));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenderDto> close(@PathVariable Long id) {
        return ResponseEntity.ok(tenderService.closeTender(id));
    }

    @PostMapping("/{id}/apply")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('PROCUREMENT', 'ADMIN')")
    public ResponseEntity<Object> applyTender(@PathVariable Long id, @RequestBody Object application) {
        // Placeholder: application submission logic
        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getApplications(@PathVariable Long id) {
        // Placeholder: get applications for tender
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/my-applications")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('PROCUREMENT', 'ADMIN')")
    public ResponseEntity<Object> getMyApplications() {
        // Placeholder: get user's applications
        return ResponseEntity.ok(List.of());
    }

    @PatchMapping("/{tid}/applications/{aid}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> approveApplication(@PathVariable Long tid, @PathVariable Long aid) {
        return ResponseEntity.ok(Map.of("message", "Application approved"));
    }

    @PatchMapping("/{tid}/applications/{aid}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> rejectApplication(@PathVariable Long tid, @PathVariable Long aid) {
        return ResponseEntity.ok(Map.of("message", "Application rejected"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tenderService.deleteTender(id);
        return ResponseEntity.noContent().build();
    }
}
