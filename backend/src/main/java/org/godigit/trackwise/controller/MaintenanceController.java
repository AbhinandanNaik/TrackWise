package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;
import org.godigit.trackwise.service.MaintenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for handling all operations related to asset maintenance logs.
 * This includes creating new logs and retrieving the history for a specific asset.
 */
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "9.Maintenance Management", description = "Endpoints for asset maintenance logs.")
public class MaintenanceController {

    // The service layer that contains the business logic for maintenance operations.
    private final MaintenanceService maintenanceService;

    /**
     * Adds a new maintenance log entry for a specific asset.
     * This action also typically sets the asset's status to UNDER_MAINTENANCE.
     * Accessible only by ADMIN role.
     * @param assetId The UUID of the asset being serviced.
     * @param request The DTO containing the details of the maintenance work.
     * @return The created maintenance log's data as a DTO.
     */
    @PostMapping("/{assetId}")
    @Operation(summary = "Add a maintenance log to an asset")
    public ResponseEntity<MaintenanceResponse> addMaintenance(
            @PathVariable UUID assetId,
            @Valid @RequestBody MaintenanceRequest request) {
        // Delegate the business logic to the MaintenanceService.
        MaintenanceResponse savedDto = maintenanceService.addMaintenance(assetId, request);
        // Return a 201 CREATED status to indicate a new resource was created.
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a list of all maintenance logs for a specific asset.
     * Accessible by USER and ADMIN roles.
     * @param assetId The UUID of the asset whose history is being requested.
     * @return A list of maintenance logs for the asset.
     */
    @GetMapping("/{assetId}")
    @Operation(summary = "List all maintenance logs for an asset")
    public ResponseEntity<List<MaintenanceResponse>> listByAsset(@PathVariable UUID assetId) {
        // Delegate the data retrieval to the MaintenanceService.
        List<MaintenanceResponse> logs = maintenanceService.listByAsset(assetId);
        // Return a 200 OK response with the list of logs.
        return ResponseEntity.ok(logs);
    }
}