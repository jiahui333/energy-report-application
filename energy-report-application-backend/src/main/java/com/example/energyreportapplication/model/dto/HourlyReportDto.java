package com.example.energyreportapplication.model.dto;

import java.math.BigDecimal;

public record HourlyReportDto (String hour, long kwhUsed, BigDecimal cost) {}
