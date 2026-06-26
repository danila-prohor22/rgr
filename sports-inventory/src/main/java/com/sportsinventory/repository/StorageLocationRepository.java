package com.sportsinventory.repository;

import com.sportsinventory.entity.StorageLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    Page<StorageLocation> findByActiveTrue(Pageable pageable);
    List<StorageLocation> findBySportTypeIgnoreCaseAndActiveTrue(String sportType);
    List<StorageLocation> findByCapacityGreaterThanEqualAndActiveTrue(Integer capacity);
}
