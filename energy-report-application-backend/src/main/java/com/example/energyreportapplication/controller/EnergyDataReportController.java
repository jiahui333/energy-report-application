package com.example.energyreportapplication.controller;

import com.example.energyreportapplication.model.dto.ReportDto;
import com.example.energyreportapplication.model.entity.ReadingType;
import com.example.energyreportapplication.repository.ReadingTypeRepository;
import com.example.energyreportapplication.service.ReportGeneratingService;
import com.example.energyreportapplication.service.XmlParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing energy data reporting.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *   <li>Ingesting XML energy data.</li>
 *   <li>Generating a report for a specific meter.</li>
 *   <li>Retrieving all available meter IDs.</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api")
public class EnergyDataReportController {

    private final XmlParsingService xmlParsingService;
    private final ReportGeneratingService reportGeneratingService;

    /**
     * Constructs the EnergyDataReportController with required services.
     *
     * @param xmlParsingService        Service to handle XML parsing and data storage.
     * @param reportGeneratingService  Service to generate reports based on meter data.
     * @param readingTypeRepository    Repository to access meter readings.
     */
    @Autowired
    public EnergyDataReportController(XmlParsingService xmlParsingService, ReportGeneratingService reportGeneratingService, ReadingTypeRepository readingTypeRepository) {
        this.xmlParsingService = xmlParsingService;
        this.reportGeneratingService = reportGeneratingService;
        this.readingTypeRepository = readingTypeRepository;
    }

    /**
     * Endpoint to ingest XML data and store it.
     * <p>
     * This endpoint accepts XML input, parses it, and stores the data for future processing.
     * </p>
     *
     * @param xmlData XML payload as a String containing meter readings.
     * @return {@link ResponseEntity} with HTTP 200 OK status upon successful processing.
     */
    @PostMapping(value = "/data", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Void> ingestData(@RequestBody String xmlData) {
        xmlParsingService.parseAndStore(xmlData);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to retrieve a report for a specific meter ID.
     * <p>
     * The generated report contains total energy usage and cost as well as hourly details for the given meter.
     * </p>
     *
     * @param meterId The unique identifier of the meter for which the report is requested.
     * @return {@link ResponseEntity} containing the {@link ReportDto} with report details.
     */
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportDto> getReport(@RequestParam String meterId) {
        ReportDto report = reportGeneratingService.getReport(meterId);
        return ResponseEntity.ok(report);
    }

    /**
     * Endpoint to retrieve all available meter IDs.
     * <p>
     * This endpoint returns a list of all meter IDs currently stored in the system.
     * </p>
     *
     * @return {@link ResponseEntity} containing a list of meter IDs.
     */
    @GetMapping(value = "/meters", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getMeters() {
        List<String> meterIds = reportGeneratingService.getAllMeterIds();
        return ResponseEntity.ok(meterIds);
    }
}
