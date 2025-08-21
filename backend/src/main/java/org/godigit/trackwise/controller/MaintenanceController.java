package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;
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
    public ResponseEntity<MaintenanceResponse> addMaintenance(
            @PathVariable UUID assetId,
            @RequestBody MaintenanceRequest request) {
        MaintenanceResponse savedDto = maintenanceService.addMaintenance(assetId, request);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // List all maintenance logs for a specific asset
    @GetMapping("/{assetId}")
    public ResponseEntity<List<MaintenanceResponse>> listByAsset(@PathVariable UUID assetId) {
        List<MaintenanceResponse> logs = maintenanceService.listByAsset(assetId);
        return ResponseEntity.ok(logs);
    }
}