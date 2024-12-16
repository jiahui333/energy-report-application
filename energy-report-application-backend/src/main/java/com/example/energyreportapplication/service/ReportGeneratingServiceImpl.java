package com.example.energyreportapplication.service;

import com.example.energyreportapplication.exception.ReportGenerationException;
import com.example.energyreportapplication.model.dto.HourlyReportDto;
import com.example.energyreportapplication.model.dto.ReportDto;
import com.example.energyreportapplication.model.entity.IntervalReading;
import com.example.energyreportapplication.model.entity.ReadingType;
import com.example.energyreportapplication.repository.IntervalReadingRepository;
import com.example.energyreportapplication.repository.ReadingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportGeneratingServiceImpl implements ReportGeneratingService {

    private static final Logger logger = LoggerFactory.getLogger(ReportGeneratingServiceImpl.class);

    private final ReadingTypeRepository readingTypeRepository;
    private final IntervalReadingRepository intervalReadingRepository;

    @Autowired
    public ReportGeneratingServiceImpl(ReadingTypeRepository readingTypeRepository,
                             IntervalReadingRepository intervalReadingRepository) {
        this.readingTypeRepository = readingTypeRepository;
        this.intervalReadingRepository = intervalReadingRepository;
    }

    @Override
    public ReportDto getReport(String meterId) {
        // Fetch ReadingType
        ReadingType readingType = readingTypeRepository.findByMeterId(meterId)
                .orElseThrow(() -> {
                    logger.error("No ReadingType found for meterId: {}", meterId);
                    return new ReportGenerationException("No ReadingType found for meterId: " + meterId);
                });

        // Fetch IntervalReadings
        List<IntervalReading> intervalReadings = intervalReadingRepository.findByReadingType_MeterId(meterId);

        if (intervalReadings.isEmpty()) {
            logger.error("No IntervalReading found for meterId: {}", meterId);
            throw new ReportGenerationException("No IntervalReading found for meterId: " + meterId);
        }

        logger.info("Found {} IntervalReadings for meterId: {}", intervalReadings.size(), meterId);

        // Group IntervalReadings by Hour Start
        Map<Long, List<IntervalReading>> IntervalReadingMapByHour = intervalReadings.stream()
                .collect(Collectors.groupingBy(intervalReading -> toHourStart(intervalReading.getStartTimestamp())));

        // Aggregate Hourly Reports
        List<HourlyReportDto> hourlyReports = IntervalReadingMapByHour.entrySet().stream()
                .map(entry -> aggregateHourlyData(entry.getKey(), entry.getValue(), readingType.getKwhPrice()))
                .sorted(Comparator.comparing(HourlyReportDto::getHour))
                .collect(Collectors.toList());

        logger.info("Successfully aggregated {} hourly reports for meterId: {}", hourlyReports.size(), meterId);

        // Calculate Total Report
        ReportDto report = calculateTotalReport(meterId, hourlyReports);
        logger.info("Successfully generated total report for meterId {}", meterId);

        return report;
    }

    private long toHourStart(long timestamp) {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.HOURS);
        return offsetDateTime.toEpochSecond();
    }

    private String formatHour(long timestamp) {
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
        return offsetDateTime.toLocalDate().toString() + " " + String.format("%02d:00", offsetDateTime.getHour());
    }

    private HourlyReportDto aggregateHourlyData(long hourStart, List<IntervalReading> readings, BigDecimal kwhPrice) {
        long totalKwh = readings.stream()
                .mapToLong(IntervalReading::getReadingValue)
                .sum();

        BigDecimal cost = kwhPrice.multiply(BigDecimal.valueOf(totalKwh));

        HourlyReportDto hourlyReportDto = new HourlyReportDto();
        hourlyReportDto.setHour(formatHour(hourStart));
        hourlyReportDto.setKWhUsed(totalKwh);
        hourlyReportDto.setCost(cost);

        return hourlyReportDto;
    }

    private ReportDto calculateTotalReport(String meterId, List<HourlyReportDto> hourlyReports) {
        long totalEnergy = hourlyReports.stream()
                .mapToLong(HourlyReportDto::getKWhUsed)
                .sum();

        BigDecimal totalCost = hourlyReports.stream()
                .map(HourlyReportDto::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportDto report = new ReportDto();
        report.setMeterId(meterId);
        report.setTotalEnergy(totalEnergy);
        report.setTotalCost(totalCost);
        report.setHourlyReports(hourlyReports);

        return report;
    }
}
