package com.example.energyreportapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<String> handleMissingDataException(MissingDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(XmlParsingException.class)
    public ResponseEntity<String> handleXmlParsingException(XmlParsingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<String> handleDatabaseOperationException(DatabaseOperationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<String> handleReportGenerationException(ReportGenerationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Report generation failed: " + e.getMessage());
    }
}
