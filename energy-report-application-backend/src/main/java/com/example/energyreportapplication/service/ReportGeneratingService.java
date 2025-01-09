package com.example.energyreportapplication.service;

import com.example.energyreportapplication.model.dto.ReportDto;

import java.util.List;

public interface ReportGeneratingService {
    ReportDto getReport(String meterId);
    List<String> getAllMeterIds();
}
