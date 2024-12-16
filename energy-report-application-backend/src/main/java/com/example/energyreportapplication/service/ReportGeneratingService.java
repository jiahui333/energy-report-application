package com.example.energyreportapplication.service;

import com.example.energyreportapplication.model.dto.ReportDto;

public interface ReportGeneratingService {
    ReportDto getReport(String meterId);
}
