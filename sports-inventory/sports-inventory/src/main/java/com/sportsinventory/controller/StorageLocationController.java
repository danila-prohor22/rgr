package com.sportsinventory.controller;

import com.sportsinventory.dto.request.StorageLocationRequest;
import com.sportsinventory.dto.response.StorageLocationResponse;
import com.sportsinventory.service.StorageLocationService;
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
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class StorageLocationController {

    private final StorageLocationService storageLocationService;

    @GetMapping
    public ResponseEntity<Page<StorageLocationResponse>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(storageLocationService.getActiveLocations(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageLocationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storageLocationService.getById(id));
    }

    @GetMapping("/sport/{sportType}")
    public ResponseEntity<List<StorageLocationResponse>> getBySportType(@PathVariable String sportType) {
        return ResponseEntity.ok(storageLocationService.getBySportType(sportType));
    }

    @GetMapping("/capacity")
    public ResponseEntity<List<StorageLocationResponse>> getByMinCapacity(
            @RequestParam(defaultValue = "0") Integer minCapacity) {
        return ResponseEntity.ok(storageLocationService.getByMinCapacity(minCapacity));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StorageLocationResponse> create(@Valid @RequestBody StorageLocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storageLocationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StorageLocationResponse> update(@PathVariable Long id,
                                                          @Valid @RequestBody StorageLocationRequest request) {
        return ResponseEntity.ok(storageLocationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storageLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
