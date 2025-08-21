package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.service.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    // Create a new asset
    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest dto) {
        AssetResponse created = assetService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Get asset by ID
    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAsset(@PathVariable UUID id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    // List assets with pagination
    @GetMapping
    public ResponseEntity<Page<AssetResponse>> listAssets(Pageable pageable) {
        return ResponseEntity.ok(assetService.list(pageable));
    }

    // Update an asset
    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable UUID id, @RequestBody AssetRequest dto) {
        return ResponseEntity.ok(assetService.update(id, dto));
    }

    // Soft delete an asset
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Assign an asset to an employee
    @PostMapping("/{assetId}/assign/{employeeId}")
    public ResponseEntity<AssetResponse> assignToEmployee(@PathVariable UUID assetId, @PathVariable UUID employeeId) {
        return ResponseEntity.ok(assetService.assignToEmployee(assetId, employeeId));
    }

    // Unassign an asset
    @PostMapping("/{assetId}/unassign")
    public ResponseEntity<AssetResponse> unassignAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(assetService.unassign(assetId));
    }

    // Find assets by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssetResponse>> findByStatus(@PathVariable AssetStatus status) {
        return ResponseEntity.ok(assetService.findByStatus(status));
    }

    // Find assets with warranty expiring between dates
    @GetMapping("/warranty-expiring")
    public ResponseEntity<List<AssetResponse>> findWithWarrantyExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(assetService.findWithWarrantyExpiringBetween(from, to));
    }
}
