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


