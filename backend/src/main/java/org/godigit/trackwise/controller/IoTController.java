package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.IoTDataRequest;
import org.godigit.trackwise.dto.IoTDataResponse;
import org.godigit.trackwise.service.IoTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iot")
@RequiredArgsConstructor
public class IoTController {

    private final IoTService iotService;

    // Ingest IoT data using the updated DTO
    @PostMapping("/ingest")
    public ResponseEntity<IoTDataResponse> ingestData(@RequestBody IoTDataRequest request) {
        IoTDataResponse savedDto = iotService.ingest(request);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    // Process sensor data for an asset (for simulation)
    @PostMapping("/process/{assetId}")
    public ResponseEntity<IoTDataResponse> processSensorData(
            @PathVariable UUID assetId,
            @RequestParam Double temperature,
            @RequestParam Double batteryLevel,
            @RequestParam Boolean inUse,
            // Add the new location parameters
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        IoTDataResponse processedData = iotService.processSensorData(assetId, temperature, batteryLevel, inUse, latitude, longitude);
        return ResponseEntity.ok(processedData);
    }

    // Start IoT simulator
    @PostMapping("/simulator/start")
    public ResponseEntity<Void> startSimulator() {
        iotService.startSimulator();
        return ResponseEntity.ok().build();
    }

    // Stop IoT simulator
    @PostMapping("/simulator/stop")
    public ResponseEntity<Void> stopSimulator() {
        iotService.stopSimulator();
        return ResponseEntity.ok().build();
    }
}