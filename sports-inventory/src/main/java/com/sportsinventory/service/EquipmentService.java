package com.sportsinventory.service;

import com.sportsinventory.dto.request.EquipmentRequest;
import com.sportsinventory.dto.response.EquipmentResponse;
import com.sportsinventory.entity.Equipment;
import com.sportsinventory.entity.StorageLocation;
import com.sportsinventory.exception.ResourceNotFoundException;
import com.sportsinventory.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final StorageLocationService storageLocationService;

    public Page<EquipmentResponse> getActiveEquipment(Pageable pageable) {
        return equipmentRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public EquipmentResponse getById(Long id) {
        return toResponse(findEquipment(id));
    }

    public List<EquipmentResponse> getByStorageLocation(Long storageLocationId) {
        return equipmentRepository.findByStorageLocationIdAndActiveTrue(storageLocationId)
                .stream().map(this::toResponse).toList();
    }

    public List<EquipmentResponse> getByCategory(String category) {
        return equipmentRepository.findByCategoryIgnoreCaseAndActiveTrue(category)
                .stream().map(this::toResponse).toList();
    }

    public List<EquipmentResponse> getByStorageLocationAndCategory(Long storageLocationId, String category) {
        return equipmentRepository.findByStorageLocationIdAndCategoryIgnoreCaseAndActiveTrue(storageLocationId, category)
                .stream().map(this::toResponse).toList();
    }

    public List<EquipmentResponse> getAvailable() {
        return equipmentRepository.findByQuantityAvailableGreaterThanAndActiveTrue(0)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public EquipmentResponse create(Long storageLocationId, EquipmentRequest request) {
        validateQuantity(request.getQuantityTotal(), request.getQuantityAvailable());
        if (equipmentRepository.existsByInventoryCode(request.getInventoryCode())) {
            throw new IllegalArgumentException("Inventory code already exists: " + request.getInventoryCode());
        }
        StorageLocation location = storageLocationService.findLocation(storageLocationId);
        Equipment equipment = Equipment.builder()
                .storageLocation(location)
                .name(request.getName())
                .inventoryCode(request.getInventoryCode())
                .description(request.getDescription())
                .category(request.getCategory())
                .conditionStatus(request.getConditionStatus())
                .quantityTotal(request.getQuantityTotal())
                .quantityAvailable(request.getQuantityAvailable())
                .replacementCost(request.getReplacementCost())
                .active(request.getActive())
                .build();
        return toResponse(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        validateQuantity(request.getQuantityTotal(), request.getQuantityAvailable());
        Equipment equipment = findEquipment(id);
        equipmentRepository.findByInventoryCode(request.getInventoryCode())
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new IllegalArgumentException("Inventory code already exists: " + request.getInventoryCode());
                });
        equipment.setName(request.getName());
        equipment.setInventoryCode(request.getInventoryCode());
        equipment.setDescription(request.getDescription());
        equipment.setCategory(request.getCategory());
        equipment.setConditionStatus(request.getConditionStatus());
        equipment.setQuantityTotal(request.getQuantityTotal());
        equipment.setQuantityAvailable(request.getQuantityAvailable());
        equipment.setReplacementCost(request.getReplacementCost());
        equipment.setActive(request.getActive());
        return toResponse(equipment);
    }

    @Transactional
    public void delete(Long id) {
        Equipment equipment = findEquipment(id);
        equipment.setActive(false);
    }

    public Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + id));
    }

    private void validateQuantity(Integer total, Integer available) {
        if (available > total) {
            throw new IllegalArgumentException("Available quantity cannot exceed total quantity");
        }
    }

    private EquipmentResponse toResponse(Equipment equipment) {
        return EquipmentResponse.builder()
                .id(equipment.getId())
                .storageLocationId(equipment.getStorageLocation().getId())
                .storageLocationName(equipment.getStorageLocation().getName())
                .name(equipment.getName())
                .inventoryCode(equipment.getInventoryCode())
                .description(equipment.getDescription())
                .category(equipment.getCategory())
                .conditionStatus(equipment.getConditionStatus())
                .quantityTotal(equipment.getQuantityTotal())
                .quantityAvailable(equipment.getQuantityAvailable())
                .replacementCost(equipment.getReplacementCost())
                .active(equipment.getActive())
                .build();
    }
}
