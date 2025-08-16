package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.service.WarrantyService;
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
    public ResponseEntity<Warranty> createOrUpdate(@RequestBody Warranty warranty) {
        Warranty saved = warrantyService.createOrUpdate(warranty);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Find warranties expiring between two dates
    @GetMapping("/expiring")
    public ResponseEntity<List<Warranty>> findExpiringBetween(
            @RequestParam("from") String from,
            @RequestParam("to") String to) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        List<Warranty> list = warrantyService.findExpiringBetween(fromDate, toDate);
        return ResponseEntity.ok(list);
    }

    // Extend warranty end date
    @PutMapping("/{id}/extend")
    public ResponseEntity<Void> extendWarranty(
            @PathVariable("id") UUID warrantyId,
            @RequestParam("newEndDate") String newEndDateStr) {

        LocalDate newEndDate = LocalDate.parse(newEndDateStr);
        warrantyService.extendWarranty(warrantyId, newEndDate);
        return ResponseEntity.ok().build();
    }
}
