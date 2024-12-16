package com.example.energyreportapplication.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class HourlyReportDto {
    private String hour;
    private long kWhUsed;
    private BigDecimal cost;
}
