package com.example.energyreportapplication.repository;

import com.example.energyreportapplication.model.entity.ReadingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadingTypeRepository extends JpaRepository<ReadingType, Long> {
    Optional<ReadingType> findByMeterId(String meterId);
}
