package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.service.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;


/**
 * Manages core CRUD operations for assets. This controller is responsible
 * for creating, retrieving, updating, and deleting asset records.
 * It does not handle assignment logic, which is managed by the CheckInOutController.
 */
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "2.Asset Management", description = "Endpoints for managing assets.")
public class AssetController {

    private final AssetService assetService;

    /**
     * Creates a new asset in the system.
     * Accessible only by users with the ADMIN role.
     * @param dto The asset data to create.
     * @return The newly created asset's data.
     */
    @PostMapping
    @Operation(summary = "Create a new asset")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest dto) {
        AssetResponse created = assetService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Finds assets whose name contains the given search term.
     * Accessible by both USER and ADMIN roles.
     * @param name The search term for the asset name.
     * @param pageable Pagination information.
     * @return A paginated list of assets matching the search term.
     */
    @GetMapping("/search")
    @Operation(summary = "Find assets by name")
    public ResponseEntity<Page<AssetResponse>> findByName(
            @RequestParam String name,
            Pageable pageable) {
        return ResponseEntity.ok(assetService.findByName(name, pageable));
    }

    /**
     * Retrieves a single asset by its unique ID.
     * Accessible by both USER and ADMIN roles.
     * @param id The UUID of the asset to retrieve.
     * @return The requested asset's data.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an asset by ID")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable UUID id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    /**
     * Retrieves a paginated list of all assets.
     * Accessible by both USER and ADMIN roles.
     * @param pageable Pagination information.
     * @return A paginated list of assets.
     */
    @GetMapping
    @Operation(summary = "List all assets with pagination")
    public ResponseEntity<Page<AssetResponse>> listAssets(Pageable pageable) {
        return ResponseEntity.ok(assetService.list(pageable));
    }

    /**
     * Updates an existing asset's details.
     * Accessible only by users with the ADMIN role.
     * @param id The UUID of the asset to update.
     * @param dto The new data for the asset.
     * @return The updated asset's data.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing asset")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable UUID id, @RequestBody AssetRequest dto) {
        return ResponseEntity.ok(assetService.update(id, dto));
    }

    /**
     * Deletes an asset from the system.
     * Accessible only by users with the ADMIN role.
     * @param id The UUID of the asset to delete.
     * @return A no-content response.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an asset")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Find assets by status
    /**
     * Finds and lists all assets that have a specific status.
     * Accessible by both USER and ADMIN roles.
     * @param status The status to filter by (e.g., AVAILABLE, ASSIGNED).
     * @return A list of assets matching the status.
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Find assets by status")
    public ResponseEntity<List<AssetResponse>> findByStatus(@PathVariable AssetStatus status) {
        return ResponseEntity.ok(assetService.findByStatus(status));
    }
}
