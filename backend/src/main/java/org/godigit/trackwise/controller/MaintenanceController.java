package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.MaintenanceRequestDTO;
import org.godigit.trackwise.dto.MaintenanceResponseDTO;
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
    public ResponseEntity<MaintenanceResponseDTO> addMaintenance(
            @PathVariable UUID assetId,
            @RequestBody MaintenanceRequestDTO request) {
        MaintenanceResponseDTO savedDto = maintenanceService.addMaintenance(assetId, request);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // List all maintenance logs for a specific asset
    @GetMapping("/{assetId}")
    public ResponseEntity<List<MaintenanceResponseDTO>> listByAsset(@PathVariable UUID assetId) {
        List<MaintenanceResponseDTO> logs = maintenanceService.listByAsset(assetId);
        return ResponseEntity.ok(logs);
    }
}