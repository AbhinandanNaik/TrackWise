package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.IoTDataRequest;
import org.godigit.trackwise.dto.IoTDataResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.IoTDataMapper;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.IoTData;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.IoTDataRepository;
import org.godigit.trackwise.service.IoTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service implementation for handling IoT data ingestion and simulation.
 * This class is responsible for receiving sensor data, saving it,
 * and broadcasting it in real-time via WebSockets.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class IoTServiceImpl implements IoTService {

  // A logger for printing messages to the console.
  private static final Logger log = LoggerFactory.getLogger(IoTServiceImpl.class);

  // Dependencies injected by the constructor.
  private final IoTDataRepository iotDataRepository;
  private final AssetRepository assetRepository;
  private final TaskScheduler taskScheduler;
  private final SimpMessagingTemplate messagingTemplate; // For WebSocket communication.

  // Fields to manage the state of the simulator.
  private ScheduledFuture<?> simulatorFuture;
  private final AtomicBoolean simulatorRunning = new AtomicBoolean(false);

  /**
   * Ingests a real data point from an external source (e.g., an IoT device).
   * @param request The DTO containing the sensor data.
   * @return A DTO representing the saved data record.
   */
  @Override
  public IoTDataResponse ingest(IoTDataRequest request) {
    // Find the asset this data belongs to, or throw an error if not found.
    Asset asset = assetRepository.findById(request.getAssetId())
            .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

    // Create a new IoTData entity to be saved.
    IoTData data = new IoTData();
    data.setAsset(asset);
    data.setTemperature(request.getTemperature());
    data.setBatteryLevel(request.getBatteryLevel());
    data.setInUse(request.getInUse());
    data.setLatitude(request.getLatitude());
    data.setLongitude(request.getLongitude());

    // Call the private helper method to handle saving and broadcasting.
    return logAndBroadcastData(data);
  }

  /**
   * Processes simulated sensor data. This method is used by the simulator.
   */
  @Override
  public IoTDataResponse processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse, Double latitude, Double longitude) {
    // Find the asset for the simulated data.
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    // Create a new IoTData entity from the simulated parameters.
    IoTData data = new IoTData();
    data.setAsset(asset);
    data.setTemperature(temperature);
    data.setBatteryLevel(batteryLevel);
    data.setInUse(inUse);
    data.setLatitude(latitude);
    data.setLongitude(longitude);

    // Call the private helper method to handle saving and broadcasting.
    return logAndBroadcastData(data);
  }

  /**
   * Starts the background job that simulates IoT data.
   */
  @Override
  public void startSimulator() {
    // Atomically check and set the running state to prevent starting it twice.
    if (simulatorRunning.compareAndSet(false, true)) {
      // Schedule the runSimulationStep method to execute every 10 seconds.
      simulatorFuture = taskScheduler.scheduleAtFixedRate(this::runSimulationStep, Duration.ofSeconds(10));
      log.info("IoT Simulator started. Pushing data every 10 seconds.");
    }
  }

  /**
   * Stops the background IoT simulator job.
   */
  @Override
  public void stopSimulator() {
    // Atomically check and set the running state to prevent stopping it twice.
    if (simulatorRunning.compareAndSet(true, false)) {
      // If the scheduled task exists, cancel it.
      if (simulatorFuture != null) {
        simulatorFuture.cancel(false);
      }
      log.info("IoT Simulator stopped");
    }
  }

  /**
   * Contains the logic for a single tick of the simulation.
   * This method is called by the scheduler.
   */
  private void runSimulationStep() {
    try {
      // Get all assets from the database.
      List<Asset> assets = assetRepository.findAll();
      if (assets.isEmpty()) {
        log.warn("Simulator running, but no assets found in the database to simulate.");
        return;
      }

      // Pick a random asset from the list to simulate data for.
      Asset assetToSimulate = assets.get((int) (Math.random() * assets.size()));

      // Generate realistic-looking random data points.
      double temp = 20 + Math.random() * 15; // Temp between 20-35 C
      double battery = 5 + Math.random() * 95; // Battery between 5-100%
      boolean inUse = Math.random() > 0.5;
      // Simulate location data around Bengaluru.
      double latitude = 12.97 + (Math.random() - 0.5) * 0.1;
      double longitude = 77.59 + (Math.random() - 0.5) * 0.1;

      // Log the simulated data to the console for visibility.
      log.info("Simulating data for asset '{}': Temp={}, Battery={}, Lat={}, Lon={}",
              assetToSimulate.getName(),
              String.format("%.2f", temp),
              String.format("%.2f", battery),
              String.format("%.4f", latitude),
              String.format("%.4f", longitude));

      // Call the service method to process this simulated data.
      processSensorData(assetToSimulate.getId(), temp, battery, inUse, latitude, longitude);

    } catch (Exception e) {
      // Catch any errors to prevent the entire scheduled task from crashing.
      log.error("Simulator step failed", e);
    }
  }

  /**
   * A private helper method to handle the common logic of saving,
   * broadcasting, and checking alerts for IoT data. This avoids code duplication.
   *
   * @param data The IoTData entity to process.
   * @return A DTO of the saved data record.
   */
  private IoTDataResponse logAndBroadcastData(IoTData data) {
    // Set the timestamp to the moment the data is processed.
    data.setTimestamp(Instant.now());

    // Save the new data record to the database.
    IoTData savedData = iotDataRepository.save(data);

    // Convert the saved entity to a DTO.
    IoTDataResponse responseDto = IoTDataMapper.toResponseDTO(savedData);

    // Push the DTO to all connected WebSocket clients on the "/topic/asset-locations" channel.
    messagingTemplate.convertAndSend("/topic/asset-locations", responseDto);

    // Business Rule: If battery is low, log a warning and potentially create a notification.
    if (data.getBatteryLevel() != null && data.getBatteryLevel() < 10.0) {
      log.warn("Low battery for asset {}: {}%", data.getAsset().getId(), data.getBatteryLevel());
      // TODO: Call NotificationService to create an in-app alert or send an email.
    }

    // Return the DTO to the original caller.
    return responseDto;
  }
}