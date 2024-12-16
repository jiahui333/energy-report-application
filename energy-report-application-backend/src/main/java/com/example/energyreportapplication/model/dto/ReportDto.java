package com.example.energyreportapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ReportDto {
    private String meterId;
    private long totalEnergy;
    private BigDecimal totalCost;
    private List<HourlyReportDto> hourlyReports;
}
