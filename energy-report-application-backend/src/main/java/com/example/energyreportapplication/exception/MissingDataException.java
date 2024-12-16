package com.example.energyreportapplication.exception;

public class MissingDataException extends RuntimeException {
    public MissingDataException(String message) {
        super(message);
    }
}
