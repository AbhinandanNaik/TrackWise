package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.WarrantyRequest;
import org.godigit.trackwise.dto.WarrantyResponse;
import org.godigit.trackwise.service.WarrantyService;
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

    // Create or update a warranty using a DTO
    @PostMapping
    public ResponseEntity<WarrantyResponse> createOrUpdate(@RequestBody WarrantyRequest request) {
        WarrantyResponse savedDto = warrantyService.createOrUpdate(request);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // Find warranties expiring between two dates
    @GetMapping("/expiring")
    public ResponseEntity<List<WarrantyResponse>> findExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<WarrantyResponse> list = warrantyService.findExpiringBetween(from, to);
        return ResponseEntity.ok(list);
    }

    // Extend warranty end date
    @PutMapping("/{id}/extend")
    public ResponseEntity<WarrantyResponse> extendWarranty(
            @PathVariable("id") UUID warrantyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        WarrantyResponse extendedWarranty = warrantyService.extendWarranty(warrantyId, newEndDate);
        return ResponseEntity.ok(extendedWarranty);
    }
}