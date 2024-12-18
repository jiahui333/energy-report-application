package com.example.energyreportapplication.service;

import com.example.energyreportapplication.exception.DatabaseOperationException;
import com.example.energyreportapplication.exception.MissingDataException;
import com.example.energyreportapplication.exception.XmlParsingException;
import com.example.energyreportapplication.model.entity.IntervalReading;
import com.example.energyreportapplication.model.entity.ReadingType;
import com.example.energyreportapplication.repository.IntervalReadingRepository;
import com.example.energyreportapplication.repository.ReadingTypeRepository;
import com.example.energyreportapplication.xmlmodel.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class XmlParsingServiceImpl implements XmlParsingService {

    private static final Logger logger = LoggerFactory.getLogger(XmlParsingServiceImpl.class);

    private final ReadingTypeRepository readingTypeRepository;
    private final IntervalReadingRepository intervalReadingRepository;
    private final XmlMapper xmlMapper;

    @Autowired
    public XmlParsingServiceImpl(ReadingTypeRepository readingTypeRepository,
                                 IntervalReadingRepository intervalReadingRepository) {
        this.readingTypeRepository = readingTypeRepository;
        this.intervalReadingRepository = intervalReadingRepository;
        this.xmlMapper = new XmlMapper();
    }

//    // For testing
//    protected XmlParsingServiceImpl(ReadingTypeRepository readingTypeRepository,
//                                 IntervalReadingRepository intervalReadingRepository,
//                                    XmlMapper xmlMapper) {
//        this.readingTypeRepository = readingTypeRepository;
//        this.intervalReadingRepository = intervalReadingRepository;
//        this.xmlMapper = xmlMapper;
//    }

    @Override
    @Transactional
    public void parseAndStore(String xmlData) {
        String meterId = null;
        try {
            Feed feed = xmlMapper.readValue(xmlData, Feed.class);
            validateFeed(feed);

            meterId = feed.getId();

            ReadingTypeXml readingTypeXml = extractReadingType(feed);
            IntervalBlock intervalBlock = extractIntervalBlock(feed);

            ReadingType readingType = findOrCreateReadingType(meterId,
                    readingTypeXml.getFlowDirection(),
                    readingTypeXml.getKWhPrice(),
                    readingTypeXml.getReadingUnit());

            List<IntervalReading> intervalReadings = mapIntervalReadings(intervalBlock, readingType);
            intervalReadingRepository.saveAll(intervalReadings);

            logger.info("Successfully parsed and stored XML data for meterId: {}", meterId);
        } catch (MissingDataException e) {
            logger.error("XML validation failed: {}", e.getMessage(), e);
            throw e;
        } catch (JsonProcessingException e) {
            String errorMessage = "Failed to parse XML data for meterId: " + meterId;
            logger.error(errorMessage, e);
            throw new XmlParsingException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "Unexpected error while parsing XML for meterId: " + meterId;
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private void validateFeed(Feed feed) {
        if (feed == null || feed.getId() == null || feed.getEntries() == null || feed.getEntries().isEmpty()) {
            throw new MissingDataException("Feed or its required fields (id, entries) are missing");
        }
    }

    private ReadingTypeXml extractReadingType(Feed feed) {
        return feed.getEntries().stream()
                .map(entry -> entry.getContent().getReadingType())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new MissingDataException("ReadingType is missing in XML"));
    }

    private IntervalBlock extractIntervalBlock(Feed feed) {
        return feed.getEntries().stream()
                .map(entry -> entry.getContent().getIntervalBlock())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new MissingDataException("IntervalBlock is missing in XML"));
    }

    private ReadingType findOrCreateReadingType(String meterId, int flowDirection, BigDecimal kwhPrice, String readingUnit) {
        try {
            return readingTypeRepository.findByMeterId(meterId)
                    .orElseGet(() -> {
                        ReadingType readingType = new ReadingType();
                        readingType.setMeterId(meterId);
                        readingType.setFlowDirection(flowDirection);
                        readingType.setKwhPrice(kwhPrice);
                        readingType.setReadingUnit(readingUnit);
                        return readingTypeRepository.save(readingType);
                    });
        } catch (Exception e) {
            String errorMessage = "Failed to find or create ReadingType for meterId: " + meterId;
            logger.error(errorMessage, e);
            throw new DatabaseOperationException(errorMessage, e);
        }
    }

    private List<IntervalReading> mapIntervalReadings(IntervalBlock intervalBlock, ReadingType readingType) {
        return intervalBlock.getIntervalReadings().stream()
                .map(intervalReadingXml -> {
                    long valueKwh = "Wh".equalsIgnoreCase(readingType.getReadingUnit())
                            ? intervalReadingXml.getValue() / 1000
                            : intervalReadingXml.getValue();

                    IntervalReading intervalReading = new IntervalReading();
                    intervalReading.setReadingType(readingType);
                    intervalReading.setStartTimestamp(intervalReadingXml.getTimePeriod().getStart());
                    intervalReading.setDurationSeconds(intervalReadingXml.getTimePeriod().getDuration());
                    intervalReading.setReadingValue(valueKwh);
                    return intervalReading;
                })
                .collect(Collectors.toList());
    }
}
