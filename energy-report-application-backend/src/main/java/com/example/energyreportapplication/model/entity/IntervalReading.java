package com.example.energyreportapplication.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class IntervalReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "reading_type_id")
    private ReadingType readingType;

    private long startTimestamp;

    private long durationSeconds;

    private long readingValue;
}
