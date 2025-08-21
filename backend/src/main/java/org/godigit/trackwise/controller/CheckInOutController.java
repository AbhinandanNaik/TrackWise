package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetScanRequest;
import org.godigit.trackwise.dto.CheckInOutRequest;
import org.godigit.trackwise.dto.CheckInOutResponse;
import org.godigit.trackwise.service.CheckInOutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//REST controller for handling asset check-in and check-out operations.

@RestController
@RequestMapping("/api/checkinout")
@RequiredArgsConstructor
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    // Use @RequestBody to accept a JSON object
    @PostMapping("/checkout")
    public ResponseEntity<CheckInOutResponse> checkoutAsset(@RequestBody CheckInOutRequest request) {
        CheckInOutResponse logDto = checkInOutService.checkoutAsset(request);
        return new ResponseEntity<>(logDto, HttpStatus.CREATED);
    }

    // Use @RequestBody here as well
    @PostMapping("/checkin")
    public ResponseEntity<CheckInOutResponse> checkinAsset(@RequestBody CheckInOutRequest request) {
        CheckInOutResponse logDto = checkInOutService.checkinAsset(request);
        return new ResponseEntity<>(logDto, HttpStatus.OK); // Use OK for updates/completions
    }

    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<List<CheckInOutResponse>> historyByAsset(@PathVariable UUID assetId) {
        List<CheckInOutResponse> logs = checkInOutService.historyByAsset(assetId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<CheckInOutResponse>> historyByEmployee(@PathVariable UUID employeeId) {
        List<CheckInOutResponse> logs = checkInOutService.historyByEmployee(employeeId);
        return ResponseEntity.ok(logs);
    }

    @PostMapping("/scan")
    public ResponseEntity<CheckInOutResponse> processScan(@RequestBody AssetScanRequest request) {
        CheckInOutResponse response = checkInOutService.processAssetScan(request);
        return ResponseEntity.ok(response);
    }
}