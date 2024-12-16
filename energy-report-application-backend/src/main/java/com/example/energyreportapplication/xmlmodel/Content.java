package com.example.energyreportapplication.xmlmodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content {
    @JacksonXmlProperty(localName = "ReadingType")
    private ReadingTypeXml readingType;
    @JacksonXmlProperty(localName = "IntervalBlock")
    private IntervalBlock intervalBlock;
}
