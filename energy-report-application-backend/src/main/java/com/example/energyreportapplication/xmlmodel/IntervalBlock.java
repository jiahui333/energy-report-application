package com.example.energyreportapplication.xmlmodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IntervalBlock {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "IntervalReading")
    private List<IntervalReadingXml> intervalReadings;
}
