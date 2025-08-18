package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetScanRequestDTO;
import org.godigit.trackwise.dto.CheckInOutRequestDTO;
import org.godigit.trackwise.dto.CheckInOutResponseDTO;
import org.godigit.trackwise.service.CheckInOutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkinout")
@RequiredArgsConstructor
public class CheckInOutController {

    private final CheckInOutService checkInOutService;

    // Use @RequestBody to accept a JSON object
    @PostMapping("/checkout")
    public ResponseEntity<CheckInOutResponseDTO> checkoutAsset(@RequestBody CheckInOutRequestDTO request) {
        CheckInOutResponseDTO logDto = checkInOutService.checkoutAsset(request);
        return new ResponseEntity<>(logDto, HttpStatus.CREATED);
    }
//vdsvsd
    // Use @RequestBody here as well
    @PostMapping("/checkin")
    public ResponseEntity<CheckInOutResponseDTO> checkinAsset(@RequestBody CheckInOutRequestDTO request) {
        CheckInOutResponseDTO logDto = checkInOutService.checkinAsset(request);
        return new ResponseEntity<>(logDto, HttpStatus.OK); // Use OK for updates/completions
    }

    @GetMapping("/asset/{assetId}/history")
    public ResponseEntity<List<CheckInOutResponseDTO>> historyByAsset(@PathVariable UUID assetId) {
        List<CheckInOutResponseDTO> logs = checkInOutService.historyByAsset(assetId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<CheckInOutResponseDTO>> historyByEmployee(@PathVariable UUID employeeId) {
        List<CheckInOutResponseDTO> logs = checkInOutService.historyByEmployee(employeeId);
        return ResponseEntity.ok(logs);
    }

    @PostMapping("/scan")
    public ResponseEntity<CheckInOutResponseDTO> processScan(@RequestBody AssetScanRequestDTO request) {
        CheckInOutResponseDTO response = checkInOutService.processAssetScan(request);
        return ResponseEntity.ok(response);
    }
}