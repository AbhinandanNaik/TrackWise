package com.company.trackwise.controller;

import com.company.trackwise.model.Asset;
import com.company.trackwise.model.AssetStatus;
import com.company.trackwise.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
    Asset created = assetService.create(asset);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  // Get asset by ID
  @GetMapping("/{id}")
  public ResponseEntity<Asset> getAsset(@PathVariable UUID id) {
    Asset asset = assetService.getById(id);
    return ResponseEntity.ok(asset);
  }

  // List all assets with pagination
  @GetMapping
  public ResponseEntity<Page<Asset>> listAssets(Pageable pageable) {
    Page<Asset> assets = assetService.list(pageable);
    return ResponseEntity.ok(assets);
  }

  // Update an asset
  @PutMapping("/{id}")
  public ResponseEntity<Asset> updateAsset(@PathVariable UUID id, @RequestBody Asset asset) {
    Asset updated = assetService.update(id, asset);
    return ResponseEntity.ok(updated);
  }

  // Soft delete (retire) an asset
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
    assetService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Assign asset to employee
  @PostMapping("/{assetId}/assign/{employeeId}")
  public ResponseEntity<Asset> assignAsset(
    @PathVariable UUID assetId,
    @PathVariable UUID employeeId) {
    Asset assigned = assetService.assignToEmployee(assetId, employeeId);
    return ResponseEntity.ok(assigned);
  }

  // Unassign asset
  @PostMapping("/{assetId}/unassign")
  public ResponseEntity<Asset> unassignAsset(@PathVariable UUID assetId) {
    Asset unassigned = assetService.unassign(assetId);
    return ResponseEntity.ok(unassigned);
  }

  // Find assets by status
  @GetMapping("/status/{status}")
  public ResponseEntity<List<Asset>> findByStatus(@PathVariable AssetStatus status) {
    List<Asset> assets = assetService.findByStatus(status);
    return ResponseEntity.ok(assets);
  }

  // Find assets with warranty expiring between dates
  @GetMapping("/warranty")
  public ResponseEntity<List<Asset>> findWarrantyExpiringBetween(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<Asset> assets = assetService.findWithWarrantyExpiringBetween(from, to);
    return ResponseEntity.ok(assets);
  }
}
