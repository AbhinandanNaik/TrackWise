package com.company.trackwise.controller;

import com.company.trackwise.model.Warranty;
import com.company.trackwise.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/warranties")
@RequiredArgsConstructor
public class WarrantyController {

  private final WarrantyService warrantyService;

  // Create or update a warranty
  @PostMapping
  public ResponseEntity<Warranty> createOrUpdateWarranty(@RequestBody Warranty warranty) {
    Warranty saved = warrantyService.createOrUpdate(warranty);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  // Find warranties expiring between dates
  @GetMapping("/expiring")
  public ResponseEntity<List<Warranty>> findExpiringBetween(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    List<Warranty> list = warrantyService.findExpiringBetween(from, to);
    return ResponseEntity.ok(list);
  }

  // Extend a warranty
  @PutMapping("/{warrantyId}/extend")
  public ResponseEntity<Void> extendWarranty(
    @PathVariable UUID warrantyId,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
    warrantyService.extendWarranty(warrantyId, newEndDate);
    return ResponseEntity.ok().build();
  }
}
