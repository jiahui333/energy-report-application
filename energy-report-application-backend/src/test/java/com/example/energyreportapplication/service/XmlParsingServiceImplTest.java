package com.example.energyreportapplication.service;

import com.example.energyreportapplication.exception.MissingDataException;
import com.example.energyreportapplication.exception.XmlParsingException;
import com.example.energyreportapplication.model.entity.ReadingType;
import com.example.energyreportapplication.repository.IntervalReadingRepository;
import com.example.energyreportapplication.repository.ReadingTypeRepository;
import com.example.energyreportapplication.xmlmodel.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class XmlParsingServiceImplTest {

    @InjectMocks
    private XmlParsingServiceImpl xmlParsingService;

    @Mock
    private ReadingTypeRepository readingTypeRepository;

    @Mock
    private IntervalReadingRepository intervalReadingRepository;

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void parseAndStore_validXml_saveIntervalReadingsAndReadingType() throws JsonProcessingException {
        String validXml = "<mocked-xml-content>";
        Feed mockFeed = createValidFeed();
        when(xmlMapper.readValue(validXml, Feed.class)).thenReturn(mockFeed);

        ReadingType mockReadingType = new ReadingType();
        when(readingTypeRepository.findByMeterId(anyString())).thenReturn(Optional.of(mockReadingType));

        xmlParsingService.parseAndStore(validXml);

        verify(readingTypeRepository).findByMeterId(anyString());
        verify(intervalReadingRepository).saveAll(anyList());
    }

    @Test
    void parseAndStore_existingReadingType_notSavingReadingType() throws JsonProcessingException {
        String validXml = "<mocked-xml-content>";
        Feed mockFeed = createValidFeed();
        when(xmlMapper.readValue(validXml, Feed.class)).thenReturn(mockFeed);

        ReadingType existingReadingType = new ReadingType();
        when(readingTypeRepository.findByMeterId(anyString())).thenReturn(Optional.of(existingReadingType));

        xmlParsingService.parseAndStore(validXml);

        verify(readingTypeRepository, never()).save(any());
    }

    @Test
    void parseAndStore_noExistingReadingType_createAndSaveNewReadingType() throws JsonProcessingException {
        String validXml = "<mocked-xml-content>";
        Feed mockFeed = createValidFeed();
        when(xmlMapper.readValue(validXml, Feed.class)).thenReturn(mockFeed);

        when(readingTypeRepository.findByMeterId(anyString())).thenReturn(Optional.empty());
        ReadingType newReadingType = new ReadingType();
        when(readingTypeRepository.save(any())).thenReturn(newReadingType);

        xmlParsingService.parseAndStore(validXml);

        verify(readingTypeRepository).save(any(ReadingType.class));
    }

    @Test
    void parseAndStore_missingId_throwMissingDataException() throws JsonProcessingException {
        String xmlWithoutId = "<mocked-xml-content>";
        Feed mockFeed = new Feed();
        when(xmlMapper.readValue(xmlWithoutId, Feed.class)).thenReturn(mockFeed);

        assertThrows(MissingDataException.class, () -> xmlParsingService.parseAndStore(xmlWithoutId));
    }

    @Test
    void parseAndStore_missingEntries_throwMissingDataException() throws JsonProcessingException {
        String xmlWithoutEntries = "<mocked-xml-content>";
        Feed mockFeed = createFeedWithoutEntries();
        when(xmlMapper.readValue(xmlWithoutEntries, Feed.class)).thenReturn(mockFeed);

        assertThrows(MissingDataException.class, () -> xmlParsingService.parseAndStore(xmlWithoutEntries));
    }

    @Test
    void parseAndStore_missingReadingType_throwMissingDataException() throws JsonProcessingException {
        String xmlWithoutReadingType = "<mocked-xml-content>";
        Feed mockFeed = createFeedWithoutReadingType();
        when(xmlMapper.readValue(xmlWithoutReadingType, Feed.class)).thenReturn(mockFeed);

        assertThrows(MissingDataException.class, () -> xmlParsingService.parseAndStore(xmlWithoutReadingType));
    }

    @Test
    void parseAndStore_invalidXml_throwXmlParsingException() throws JsonProcessingException {
        String invalidXml = "<invalid-xml>";
        when(xmlMapper.readValue(invalidXml, Feed.class)).thenThrow(JsonProcessingException.class);

        assertThrows(XmlParsingException.class, () -> xmlParsingService.parseAndStore(invalidXml));
        verifyNoInteractions(readingTypeRepository, intervalReadingRepository);
    }

    // Helper methods to create mock Feed objects
    private Feed createValidFeed() {
        Feed feed = new Feed();
        feed.setId("12345");

        Entry entry = new Entry();
        Content content = new Content();
        ReadingTypeXml readingType = new ReadingTypeXml();
        readingType.setFlowDirection(1);
        readingType.setKWhPrice(BigDecimal.valueOf(0.10));
        readingType.setReadingUnit("kWh");
        content.setReadingType(readingType);

        IntervalBlock intervalBlock = new IntervalBlock();
        IntervalReadingXml intervalReading = new IntervalReadingXml();
        intervalReading.setValue(1000);
        intervalReading.setTimePeriod(new TimePeriod(0L, 900L));
        intervalBlock.setIntervalReadings(List.of(intervalReading));
        content.setIntervalBlock(intervalBlock);

        entry.setContent(content);
        feed.setEntries(List.of(entry));
        return feed;
    }

    private Feed createFeedWithoutEntries() {
        Feed feed = new Feed();
        feed.setId("12345");
        feed.setEntries(null);
        return feed;
    }

    private Feed createFeedWithoutReadingType() {
        Feed feed = new Feed();
        feed.setId("12345");

        Entry entry = new Entry();
        Content content = new Content();
        content.setReadingType(null);
        content.setIntervalBlock(new IntervalBlock());
        entry.setContent(content);

        feed.setEntries(List.of(entry));
        return feed;
    }
}
