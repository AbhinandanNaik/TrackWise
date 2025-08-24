package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetScanRequest;
import org.godigit.trackwise.dto.CheckInOutRequest;
import org.godigit.trackwise.dto.CheckInOutResponse;
import org.godigit.trackwise.service.CheckInOutService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


/**
 * Handles all asset check-in and check-out operations. This is the central
 * place for managing asset assignments and viewing their history.
 */
@RestController
@RequestMapping("/api/checkinout")
@RequiredArgsConstructor
@Tag(name = "4. Check-in / Check-out", description = "Endpoints for asset assignment and history.")
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    /**
     * Checks out an asset to an employee, creating a new log entry and assigning the asset.
     * Accessible only by ADMIN role.
     * @param request DTO containing the assetId and employeeId.
     * @return The created check-out log entry.
     */
    @PostMapping("/checkout")
    @Operation(summary = "Check out an asset to an employee")
    public ResponseEntity<CheckInOutResponse> checkoutAsset(@RequestBody CheckInOutRequest request) {
        CheckInOutResponse logDto = checkInOutService.checkoutAsset(request);
        return new ResponseEntity<>(logDto, HttpStatus.CREATED);
    }

    /**
     * Checks in a previously assigned asset, making it available again.
     * Accessible only by ADMIN role.
     * @param request DTO containing the assetId and employeeId.
     * @return The updated log entry with the check-in time.
     */
    @PostMapping("/checkin")
    @Operation(summary = "Check in a previously assigned asset")
    public ResponseEntity<CheckInOutResponse> checkinAsset(@RequestBody CheckInOutRequest request) {
        CheckInOutResponse logDto = checkInOutService.checkinAsset(request);
        return ResponseEntity.ok(logDto);
    }

    /**
     * A smart endpoint that automatically processes a check-in or check-out
     * based on the asset's current status. Ideal for QR code scanning.
     * Accessible only by ADMIN role.
     * @param request DTO containing the assetId and employeeId from the scan.
     * @return The resulting log entry.
     */
    @PostMapping("/scan")
    @Operation(summary = "Smart scan an asset to check it in or out")
    public ResponseEntity<CheckInOutResponse> processScan(@RequestBody AssetScanRequest request) {
        CheckInOutResponse response = checkInOutService.processAssetScan(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the full check-in/out history for a specific asset.
     * Accessible by USER and ADMIN roles.
     * @param assetId The UUID of the asset.
     * @return A list of check-in/out log entries for the asset.
     */
    @GetMapping("/asset/{assetId}/history")
    @Operation(summary = "Get history for a specific asset")
    public ResponseEntity<List<CheckInOutResponse>> historyByAsset(@PathVariable UUID assetId) {
        List<CheckInOutResponse> logs = checkInOutService.historyByAsset(assetId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Retrieves the full check-in/out history for a specific employee.
     * Accessible by USER and ADMIN roles.
     * @param employeeId The UUID of the employee.
     * @return A list of check-in/out log entries for the employee.
     */
    @GetMapping("/employee/{employeeId}/history")
    @Operation(summary = "Get history for a specific employee")
    public ResponseEntity<List<CheckInOutResponse>> historyByEmployee(@PathVariable UUID employeeId) {
        List<CheckInOutResponse> logs = checkInOutService.historyByEmployee(employeeId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Retrieves the currently checked-out asset for a specific employee.
     * Accessible by USER and ADMIN roles.
     * @param employeeId The UUID of the employee.
     * @return The currently open check-out log, or 404 if none exists.
     */
    @GetMapping("/employee/{employeeId}/current")
    @Operation(summary = "Get the current checked-out asset for an employee")
    public ResponseEntity<CheckInOutResponse> getCurrentCheckoutByEmployee(@PathVariable UUID employeeId) {
        CheckInOutResponse log = checkInOutService.findCurrentCheckoutByEmployee(employeeId);
        return log != null ? ResponseEntity.ok(log) : ResponseEntity.notFound().build();
    }

    /**
     * Finds all assets that are considered overdue (checked out before a given date and not yet returned).
     * Accessible only by ADMIN role.
     * @param since The date to check against.
     * @return A list of overdue check-out logs.
     */
    @GetMapping("/overdue")
    @Operation(summary = "Find all overdue assets")
    public ResponseEntity<List<CheckInOutResponse>> getOverdueAssets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        List<CheckInOutResponse> logs = checkInOutService.findOverdueAssets(since);
        return ResponseEntity.ok(logs);
    }
}