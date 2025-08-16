package com.company.trackwise.controller;

import com.company.trackwise.model.CheckInOutLog;
import com.company.trackwise.service.CheckInOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkinout")
@RequiredArgsConstructor
public class CheckInOutController {

  private final CheckInOutService checkInOutService;

  // Checkout an asset to an employee
  @PostMapping("/checkout")
  public ResponseEntity<CheckInOutLog> checkoutAsset(
    @RequestParam UUID assetId,
    @RequestParam UUID employeeId) {
    CheckInOutLog log = checkInOutService.checkoutAsset(assetId, employeeId);
    return new ResponseEntity<>(log, HttpStatus.CREATED);
  }

  // Checkin an asset from an employee
  @PostMapping("/checkin")
  public ResponseEntity<CheckInOutLog> checkinAsset(
    @RequestParam UUID assetId,
    @RequestParam UUID employeeId) {
    CheckInOutLog log = checkInOutService.checkinAsset(assetId, employeeId);
    return new ResponseEntity<>(log, HttpStatus.CREATED);
  }

  // Get check-in/out history for a specific asset
  @GetMapping("/asset/{assetId}/history")
  public ResponseEntity<List<CheckInOutLog>> historyByAsset(@PathVariable UUID assetId) {
    List<CheckInOutLog> history = checkInOutService.historyByAsset(assetId);
    return ResponseEntity.ok(history);
  }

  // Get check-in/out history for a specific employee
  @GetMapping("/employee/{employeeId}/history")
  public ResponseEntity<List<CheckInOutLog>> historyByEmployee(@PathVariable UUID employeeId) {
    List<CheckInOutLog> history = checkInOutService.historyByEmployee(employeeId);
    return ResponseEntity.ok(history);
  }
}
