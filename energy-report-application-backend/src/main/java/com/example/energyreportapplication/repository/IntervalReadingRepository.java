package com.example.energyreportapplication.repository;

import com.example.energyreportapplication.model.entity.IntervalReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntervalReadingRepository extends JpaRepository<IntervalReading, Integer> {
    List<IntervalReading> findByReadingType_MeterId(String meterId);
}
