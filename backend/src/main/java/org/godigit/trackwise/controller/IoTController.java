package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.IoTData;
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

    // Ingest IoT data
    @PostMapping("/ingest")
    public ResponseEntity<IoTData> ingestData(@RequestBody IoTData data) {
        IoTData saved = iotService.ingest(data);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Process sensor data for an asset
    @PostMapping("/process/{assetId}")
    public ResponseEntity<Void> processSensorData(
            @PathVariable UUID assetId,
            @RequestParam Double temperature,
            @RequestParam Double batteryLevel,
            @RequestParam Boolean inUse) {
        iotService.processSensorData(assetId, temperature, batteryLevel, inUse);
        return ResponseEntity.ok().build();
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
