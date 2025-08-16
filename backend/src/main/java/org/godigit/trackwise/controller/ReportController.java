package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Generate warranty expiry report between two dates
    @GetMapping("/warranty-expiry")
    public ResponseEntity<byte[]> generateWarrantyExpiryReport(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        byte[] data = reportService.generateWarrantyExpiryReport(fromDate, toDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=warranty_expiry_report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }

    // Generate asset aging report for assets older than given days
    @GetMapping("/asset-aging")
    public ResponseEntity<byte[]> generateAssetAgingReport(
            @RequestParam("olderThanDays") int olderThanDays) {

        byte[] data = reportService.generateAssetAgingReport(olderThanDays);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asset_aging_report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(data);
    }
}
