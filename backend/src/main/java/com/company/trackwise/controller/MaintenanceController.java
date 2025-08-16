package com.company.trackwise.controller;

import com.company.trackwise.model.MaintenanceLog;
import com.company.trackwise.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

  private final MaintenanceService maintenanceService;

  // Add maintenance log for an asset
  @PostMapping("/asset/{assetId}")
  public ResponseEntity<MaintenanceLog> addMaintenanceLog(
    @PathVariable UUID assetId,
    @RequestBody MaintenanceLog log) {
    MaintenanceLog saved = maintenanceService.addMaintenance(assetId, log);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  // Get all maintenance logs for an asset
  @GetMapping("/asset/{assetId}")
  public ResponseEntity<List<MaintenanceLog>> getMaintenanceLogs(@PathVariable UUID assetId) {
    List<MaintenanceLog> logs = maintenanceService.listByAsset(assetId);
    return ResponseEntity.ok(logs);
  }
}
