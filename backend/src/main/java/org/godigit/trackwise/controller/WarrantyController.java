package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * REST controller for managing all operations related to asset warranties,
 * including creation, updates, and searching for expiring warranties.
 */
@RestController
@RequestMapping("/api/warranties")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "8. Warranty Management", description = "Endpoints for managing warranties.")
public class WarrantyController {

    // The service layer that contains all the business logic for warranties.
    private final WarrantyService warrantyService;

    /**
     * Creates or updates a warranty for an asset.
     * Accessible only by ADMIN role.
     * @param request The DTO containing the warranty details.
     * @return The created or updated warranty's data as a DTO.
     */
    @PostMapping
    @Operation(summary = "Create or update a warranty")
    public ResponseEntity<WarrantyResponse> createOrUpdate(@RequestBody WarrantyRequest request) {
        // Delegate the logic to the service layer.
        WarrantyResponse savedDto = warrantyService.createOrUpdate(request);
        // Return a 201 CREATED status to indicate a new resource was created.
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    /**
     * Finds warranties that expire within a given date range.
     * Accessible by USER and ADMIN roles.
     * @param from The start date of the search period.
     * @param to The end date of the search period.
     * @return A list of warranties expiring in the specified range.
     */
    @GetMapping("/search")
    @Operation(summary = "Find warranties by expiry date range")
    public ResponseEntity<List<WarrantyResponse>> findExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        // Delegate the search logic to the service layer.
        List<WarrantyResponse> list = warrantyService.findExpiringBetween(from, to);
        // Return a 200 OK response with the list of found warranties.
        return ResponseEntity.ok(list);
    }

    /**
     * Extends the end date of an existing warranty.
     * Accessible only by ADMIN role.
     * @param warrantyId The UUID of the warranty to extend.
     * @param newEndDate The new expiry date for the warranty.
     * @return The updated warranty's data as a DTO.
     */
    @PutMapping("/{id}/extend")
    @Operation(summary = "Extend a warranty's end date")
    public ResponseEntity<WarrantyResponse> extendWarranty(
            @PathVariable("id") UUID warrantyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        // Delegate the update logic to the service layer.
        WarrantyResponse extendedWarranty = warrantyService.extendWarranty(warrantyId, newEndDate);
        // Return a 200 OK response with the updated warranty.
        return ResponseEntity.ok(extendedWarranty);
    }
}