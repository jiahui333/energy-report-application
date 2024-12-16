package com.example.energyreportapplication.xmlmodel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntervalReadingXml {
    private TimePeriod timePeriod;
    private long value;
}
