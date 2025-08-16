package com.company.trackwise.controller;

import com.company.trackwise.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

  // Generate warranty expiry report
  @GetMapping("/warranty-expiry")
  public ResponseEntity<byte[]> warrantyExpiryReport(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    byte[] report = reportService.generateWarrantyExpiryReport(from, to);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=warranty_expiry_report.csv")
      .contentType(MediaType.TEXT_PLAIN)
      .body(report);
  }

  // Generate asset aging report
  @GetMapping("/asset-aging")
  public ResponseEntity<byte[]> assetAgingReport(
    @RequestParam int olderThanDays) {

    byte[] report = reportService.generateAssetAgingReport(olderThanDays);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asset_aging_report.csv")
      .contentType(MediaType.TEXT_PLAIN)
      .body(report);
  }
}
