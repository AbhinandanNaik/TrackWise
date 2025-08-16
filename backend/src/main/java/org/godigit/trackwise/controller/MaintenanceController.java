package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.service.MaintenanceService;
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

    // Add a maintenance log to an asset
    @PostMapping("/{assetId}")
    public ResponseEntity<MaintenanceLog> addMaintenance(
            @PathVariable UUID assetId,
            @RequestBody MaintenanceLog log) {
        MaintenanceLog saved = maintenanceService.addMaintenance(assetId, log);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // List all maintenance logs for a specific asset
    @GetMapping("/{assetId}")
    public ResponseEntity<List<MaintenanceLog>> listByAsset(@PathVariable UUID assetId) {
        List<MaintenanceLog> logs = maintenanceService.listByAsset(assetId);
        return ResponseEntity.ok(logs);
    }
}
