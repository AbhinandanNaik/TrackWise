package com.company.trackwise.controller;

import com.company.trackwise.model.IoTData;
import com.company.trackwise.service.IoTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iot")
@RequiredArgsConstructor
public class IoTController {

  private final IoTService iotService;

  // Ingest raw IoT data
  @PostMapping("/ingest")
  public ResponseEntity<IoTData> ingestData(@RequestBody IoTData data) {
    IoTData saved = iotService.ingest(data);
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }

  // Process sensor data for a specific asset
  @PostMapping("/sensor/{assetId}")
  public ResponseEntity<Void> processSensorData(
    @PathVariable UUID assetId,
    @RequestParam(required = false) Double temperature,
    @RequestParam(required = false) Double batteryLevel,
    @RequestParam(required = false) Boolean inUse) {
    iotService.processSensorData(assetId, temperature, batteryLevel, inUse);
    return ResponseEntity.ok().build();
  }

  // Start IoT simulator
  @PostMapping("/simulator/start")
  public ResponseEntity<String> startSimulator() {
    iotService.startSimulator();
    return ResponseEntity.ok("IoT Simulator started");
  }

  // Stop IoT simulator
  @PostMapping("/simulator/stop")
  public ResponseEntity<String> stopSimulator() {
    iotService.stopSimulator();
    return ResponseEntity.ok("IoT Simulator stopped");
  }
}
