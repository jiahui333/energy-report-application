# Application Overview
A solution where you parse the xml files and build a report which contains the hourly energy values, the price per hour and the sum of energy and price per meter.

## How to run
- Frontend: npm run dev
- Backend: click run on EnergyReportApplication


---

# Backend API Documentation

## Overview
This backend provides a REST API for managing energy data. The main functionalities include:
- Ingesting XML energy data.
- Generating reports for specific meters.
- Retrieving all available meter IDs.

## Endpoints

| HTTP Method | Endpoint       | Description                                   | Request Body / Params                     | Response                       | Content Type         |
|-------------|----------------|-----------------------------------------------|-------------------------------------------|--------------------------------|----------------------|
| POST        | `/api/data`    | Ingest XML data for energy readings.          | XML String in the request body.           | HTTP 200 OK                   | `application/xml`    |
| GET         | `/api/report`  | Retrieve a report for a specific meter.       | Query param: `meterId` (string).          | JSON: `ReportDto`             | `application/json`   |
| GET         | `/api/meters`  | Retrieve all available meter IDs.             | None                                      | JSON: List of meter IDs        | `application/json`   |


---

# Application Flow Diagram

![flow](./asset/EnergyReportApplicationFlow.jpg)

---

# Database Schema

```
+-------------------+               +---------------------+
|  ReadingType      |               |  IntervalReading    |
+-------------------+               +---------------------+
| id (PK)           |<-one to many->| id (PK)             |
| meterId           |               | reading_type_id (FK)|
| flowDirection     |               | startTimestamp      |
| kwhPrice          |               | durationSeconds     |
| readingUnit       |               | readingValue        |
+-------------------+               +---------------------+
```