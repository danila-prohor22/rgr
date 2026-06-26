package com.sportsinventory.controller;

import com.sportsinventory.dto.request.EquipmentRequest;
import com.sportsinventory.dto.response.EquipmentResponse;
import com.sportsinventory.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<Page<EquipmentResponse>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(equipmentService.getActiveEquipment(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getById(id));
    }

    @GetMapping("/location/{storageLocationId}")
    public ResponseEntity<List<EquipmentResponse>> getByLocation(@PathVariable Long storageLocationId) {
        return ResponseEntity.ok(equipmentService.getByStorageLocation(storageLocationId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EquipmentResponse>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(equipmentService.getByCategory(category));
    }

    @GetMapping("/location/{storageLocationId}/category/{category}")
    public ResponseEntity<List<EquipmentResponse>> getByLocationAndCategory(
            @PathVariable Long storageLocationId,
            @PathVariable String category) {
        return ResponseEntity.ok(equipmentService.getByStorageLocationAndCategory(storageLocationId, category));
    }

    @GetMapping("/available")
    public ResponseEntity<List<EquipmentResponse>> getAvailable() {
        return ResponseEntity.ok(equipmentService.getAvailable());
    }

    @PostMapping("/location/{storageLocationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> create(@PathVariable Long storageLocationId,
                                                    @Valid @RequestBody EquipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.create(storageLocationId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody EquipmentRequest request) {
        return ResponseEntity.ok(equipmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
