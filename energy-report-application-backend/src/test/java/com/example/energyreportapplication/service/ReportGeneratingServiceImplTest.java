package com.example.energyreportapplication.service;

import com.example.energyreportapplication.exception.ReportGenerationException;
import com.example.energyreportapplication.model.dto.ReportDto;
import com.example.energyreportapplication.model.entity.IntervalReading;
import com.example.energyreportapplication.model.entity.ReadingType;
import com.example.energyreportapplication.repository.IntervalReadingRepository;
import com.example.energyreportapplication.repository.ReadingTypeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportGeneratingServiceImplTest {

    @InjectMocks
    private ReportGeneratingServiceImpl reportGeneratingService;

    @Mock
    private ReadingTypeRepository readingTypeRepository;

    @Mock
    private IntervalReadingRepository intervalReadingRepository;

    @Test
    void getReport_shouldReturnReportForValidMeterId() {
        String meterId = "12345";

        ReadingType readingType = new ReadingType();
        readingType.setMeterId(meterId);
        readingType.setKwhPrice(BigDecimal.valueOf(0.10));
        when(readingTypeRepository.findByMeterId(meterId)).thenReturn(Optional.of(readingType));

        IntervalReading intervalReading = new IntervalReading();
        intervalReading.setStartTimestamp(3600L); // 1st hour
        intervalReading.setReadingValue(100L);
        when(intervalReadingRepository.findByReadingType_MeterId(meterId)).thenReturn(List.of(intervalReading));

        ReportDto report = reportGeneratingService.getReport(meterId);

        assertEquals(meterId, report.getMeterId());
        assertEquals(100L, report.getTotalEnergy());
        assertEquals(BigDecimal.valueOf(10.0), report.getTotalCost());
        assertEquals(1, report.getHourlyReports().size());
    }

    @Test
    void getReport_shouldThrowReportGenerationExceptionWhenNoReadingType() {
        String meterId = "12345";
        when(readingTypeRepository.findByMeterId(meterId)).thenReturn(Optional.empty());

        ReportGenerationException exception = assertThrows(ReportGenerationException.class, () -> reportGeneratingService.getReport(meterId));
        assertEquals("No ReadingType found for meterId: " + meterId, exception.getMessage());
    }

    @Test
    void getReport_shouldThrowReportGenerationExceptionWhenNoIntervalReadings() {
        String meterId = "12345";

        ReadingType readingType = new ReadingType();
        readingType.setMeterId(meterId);
        when(readingTypeRepository.findByMeterId(meterId)).thenReturn(Optional.of(readingType));

        when(intervalReadingRepository.findByReadingType_MeterId(meterId)).thenReturn(Collections.emptyList());

        ReportGenerationException exception = assertThrows(ReportGenerationException.class, () -> reportGeneratingService.getReport(meterId));
        assertEquals("No IntervalReading found for meterId: " + meterId, exception.getMessage());
    }

    @Test
    void getReport_shouldGroupIntervalReadingsByHour() {
        String meterId = "12345";

        ReadingType readingType = new ReadingType();
        readingType.setMeterId(meterId);
        readingType.setKwhPrice(BigDecimal.valueOf(0.10));
        when(readingTypeRepository.findByMeterId(meterId)).thenReturn(Optional.of(readingType));

        IntervalReading reading1 = new IntervalReading();
        reading1.setStartTimestamp(3600L); // Hour 1
        reading1.setReadingValue(100L);

        IntervalReading reading2 = new IntervalReading();
        reading2.setStartTimestamp(7200L); // Hour 2
        reading2.setReadingValue(200L);

        when(intervalReadingRepository.findByReadingType_MeterId(meterId)).thenReturn(List.of(reading1, reading2));

        ReportDto report = reportGeneratingService.getReport(meterId);

        assertEquals(2, report.getHourlyReports().size());
        assertEquals(300L, report.getTotalEnergy());
        assertEquals(BigDecimal.valueOf(30.0), report.getTotalCost());
    }
}

