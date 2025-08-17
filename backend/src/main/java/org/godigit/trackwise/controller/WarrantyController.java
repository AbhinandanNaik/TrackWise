package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.WarrantyRequestDTO;
import org.godigit.trackwise.dto.WarrantyResponseDTO;
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
    public ResponseEntity<WarrantyResponseDTO> createOrUpdate(@RequestBody WarrantyRequestDTO request) {
        WarrantyResponseDTO savedDto = warrantyService.createOrUpdate(request);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // Find warranties expiring between two dates
    @GetMapping("/expiring")
    public ResponseEntity<List<WarrantyResponseDTO>> findExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<WarrantyResponseDTO> list = warrantyService.findExpiringBetween(from, to);
        return ResponseEntity.ok(list);
    }

    // Extend warranty end date
    @PutMapping("/{id}/extend")
    public ResponseEntity<WarrantyResponseDTO> extendWarranty(
            @PathVariable("id") UUID warrantyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        WarrantyResponseDTO extendedWarranty = warrantyService.extendWarranty(warrantyId, newEndDate);
        return ResponseEntity.ok(extendedWarranty);
    }
}