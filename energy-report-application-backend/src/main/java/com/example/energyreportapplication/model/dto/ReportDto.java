package com.example.energyreportapplication.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReportDto (String meterId, long totalEnergy, BigDecimal totalCost, List<HourlyReportDto> hourlyReports) {}
