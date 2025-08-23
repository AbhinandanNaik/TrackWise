package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.IoTDataRequest;
import org.godigit.trackwise.dto.IoTDataResponse;
import org.godigit.trackwise.service.IoTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for handling all IoT-related operations.
 * This includes ingesting real sensor data and managing the data simulator.
 */
@RestController
@RequestMapping("/api/iot")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "5. IoT Management", description = "Endpoints for IoT data ingestion and simulation.")
public class IoTController {

    // The service layer that contains all the business logic for IoT operations.
    private final IoTService iotService;

    /**
     * Ingests a single data point from a real IoT device.
     * Accessible only by ADMIN role (or a dedicated IoT device role).
     * @param request The DTO containing the sensor readings (temp, battery, location, etc.).
     * @return The saved data record as a DTO.
     */
    @PostMapping("/ingest")
    @Operation(summary = "Ingest real-time IoT sensor data")
    public ResponseEntity<IoTDataResponse> ingestData(@RequestBody IoTDataRequest request) {
        // Delegate the business logic to the IoTService.
        IoTDataResponse savedDto = iotService.ingest(request);
        // Return a 201 CREATED status to indicate a new resource was created.
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    /**
     * A convenience endpoint to process and log simulated sensor data for a specific asset.
     * This is primarily used for testing and development.
     * Accessible only by ADMIN role.
     * @param assetId The UUID of the asset to simulate data for.
     * @param temperature The simulated temperature.
     * @param batteryLevel The simulated battery level.
     * @param inUse The simulated in-use status.
     * @param latitude The simulated latitude.
     * @param longitude The simulated longitude.
     * @return The saved data record as a DTO.
     */
    @PostMapping("/process/{assetId}")
    @Operation(summary = "Process a simulated sensor data point")
    public ResponseEntity<IoTDataResponse> processSensorData(
            @PathVariable UUID assetId,
            @RequestParam Double temperature,
            @RequestParam Double batteryLevel,
            @RequestParam Boolean inUse,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        // Delegate the logic to the service layer.
        IoTDataResponse processedData = iotService.processSensorData(assetId, temperature, batteryLevel, inUse, latitude, longitude);
        // Return a 200 OK status.
        return ResponseEntity.ok(processedData);
    }

    /**
     * Starts the background IoT data simulator.
     * Accessible only by ADMIN role.
     * @return An empty 200 OK response.
     */
    @PostMapping("/simulator/start")
    @Operation(summary = "Start the IoT data simulator")
    public ResponseEntity<Void> startSimulator() {
        // Call the service to start the scheduled background job.
        iotService.startSimulator();
        return ResponseEntity.ok().build();
    }

    /**
     * Stops the background IoT data simulator.
     * Accessible only by ADMIN role.
     * @return An empty 200 OK response.
     */
    @PostMapping("/simulator/stop")
    @Operation(summary = "Stop the IoT data simulator")
    public ResponseEntity<Void> stopSimulator() {
        // Call the service to stop the scheduled background job.
        iotService.stopSimulator();
        return ResponseEntity.ok().build();
    }
}