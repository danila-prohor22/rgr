package com.sportsinventory.service;

import com.sportsinventory.dto.request.StorageLocationRequest;
import com.sportsinventory.dto.response.StorageLocationResponse;
import com.sportsinventory.entity.StorageLocation;
import com.sportsinventory.exception.ResourceNotFoundException;
import com.sportsinventory.repository.StorageLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;

    public Page<StorageLocationResponse> getActiveLocations(Pageable pageable) {
        return storageLocationRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public StorageLocationResponse getById(Long id) {
        return toResponse(findLocation(id));
    }

    public List<StorageLocationResponse> getBySportType(String sportType) {
        return storageLocationRepository.findBySportTypeIgnoreCaseAndActiveTrue(sportType)
                .stream().map(this::toResponse).toList();
    }

    public List<StorageLocationResponse> getByMinCapacity(Integer minCapacity) {
        return storageLocationRepository.findByCapacityGreaterThanEqualAndActiveTrue(minCapacity)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StorageLocationResponse create(StorageLocationRequest request) {
        StorageLocation location = StorageLocation.builder()
                .name(request.getName())
                .address(request.getAddress())
                .sportType(request.getSportType())
                .capacity(request.getCapacity())
                .active(request.getActive())
                .build();
        return toResponse(storageLocationRepository.save(location));
    }

    @Transactional
    public StorageLocationResponse update(Long id, StorageLocationRequest request) {
        StorageLocation location = findLocation(id);
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setSportType(request.getSportType());
        location.setCapacity(request.getCapacity());
        location.setActive(request.getActive());
        return toResponse(location);
    }

    @Transactional
    public void delete(Long id) {
        StorageLocation location = findLocation(id);
        location.setActive(false);
    }

    public StorageLocation findLocation(Long id) {
        return storageLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storage location not found: " + id));
    }

    private StorageLocationResponse toResponse(StorageLocation location) {
        return StorageLocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .sportType(location.getSportType())
                .capacity(location.getCapacity())
                .active(location.getActive())
                .build();
    }
}
