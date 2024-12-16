package com.example.energyreportapplication.xmlmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReadingTypeXml {
    private int flowDirection;
    @JsonProperty("kWhPrice")
    private BigDecimal kWhPrice;
    private String readingUnit;
}
