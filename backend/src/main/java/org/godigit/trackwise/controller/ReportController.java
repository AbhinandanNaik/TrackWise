package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller dedicated to generating and serving downloadable reports.
 * This controller handles requests for data exports in formats like CSV.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "7. Reporting", description = "Endpoints for generating downloadable reports.")
public class ReportController {

    // The service layer that contains the business logic for report generation.
    private final ReportService reportService;

    /**
     * Generates a CSV report of all warranties expiring within a given date range.
     * Accessible only by ADMIN role.
     * @param from The start date of the reporting period.
     * @param to The end date of the reporting period.
     * @return A byte array representing the CSV file, configured for download.
     */
    @GetMapping("/warranty-expiry")
    @Operation(summary = "Generate a warranty expiry CSV report")
    public ResponseEntity<byte[]> generateWarrantyExpiryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        // Delegate the report generation logic to the service layer.
        byte[] data = reportService.generateWarrantyExpiryReport(from, to);

        // Build the HTTP response.
        return ResponseEntity.ok()
                // Set the Content-Disposition header to prompt a file download.
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=warranty_expiry_report.csv")
                // Set the content type to indicate a plain text/CSV file.
                .contentType(MediaType.TEXT_PLAIN)
                // Attach the CSV data as the response body.
                .body(data);
    }

    /**
     * Generates a CSV report of all assets older than a specified number of days.
     * Accessible only by ADMIN role.
     * @param olderThanDays The age threshold in days.
     * @return A byte array representing the CSV file, configured for download.
     */
    @GetMapping("/asset-aging")
    @Operation(summary = "Generate an asset aging CSV report")
    public ResponseEntity<byte[]> generateAssetAgingReport(
            @RequestParam("olderThanDays") int olderThanDays) {

        // Delegate the report generation logic to the service layer.
        byte[] data = reportService.generateAssetAgingReport(olderThanDays);

        // Build the HTTP response with the correct headers for a file download.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asset_aging_report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }
}