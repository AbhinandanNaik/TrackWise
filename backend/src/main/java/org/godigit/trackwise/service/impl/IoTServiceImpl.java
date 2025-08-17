package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.IoTDataRequestDTO;
import org.godigit.trackwise.dto.IoTDataResponseDTO;
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

@Service
@RequiredArgsConstructor
@Transactional
public class IoTServiceImpl implements IoTService {

  private static final Logger log = LoggerFactory.getLogger(IoTServiceImpl.class);

  private final IoTDataRepository iotDataRepository;
  private final AssetRepository assetRepository;
  private final TaskScheduler taskScheduler;
  private final SimpMessagingTemplate messagingTemplate; // For WebSocket communication

  private ScheduledFuture<?> simulatorFuture;
  private final AtomicBoolean simulatorRunning = new AtomicBoolean(false);

  @Override
  public IoTDataResponseDTO ingest(IoTDataRequestDTO request) {
    Asset asset = assetRepository.findById(request.getAssetId())
            .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

    IoTData data = new IoTData();
    data.setAsset(asset);
    data.setTemperature(request.getTemperature());
    data.setBatteryLevel(request.getBatteryLevel());
    data.setInUse(request.getInUse());
    data.setLatitude(request.getLatitude());   // Set location data
    data.setLongitude(request.getLongitude()); // Set location data
    data.setTimestamp(Instant.now());

    IoTData savedData = iotDataRepository.save(data);

    // After saving, push the update to all connected WebSocket clients
    messagingTemplate.convertAndSend("/topic/asset-locations", IoTDataMapper.toResponseDTO(savedData));

    // Example business logic for alerts
    if (request.getBatteryLevel() != null && request.getBatteryLevel() < 10.0) {
      log.warn("Low battery for asset {}: {}%", asset.getId(), request.getBatteryLevel());
      // TODO: Create notification/alert here
    }

    return IoTDataMapper.toResponseDTO(savedData);
  }

  @Override
  public IoTDataResponseDTO processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse, Double latitude, Double longitude) {
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    IoTData d = new IoTData();
    d.setAsset(asset);
    d.setTemperature(temperature);
    d.setBatteryLevel(batteryLevel);
    d.setInUse(inUse);
    d.setLatitude(latitude);
    d.setLongitude(longitude);
    d.setTimestamp(Instant.now());

    IoTData savedData = iotDataRepository.save(d);

    // Push the update to all connected WebSocket clients
    messagingTemplate.convertAndSend("/topic/asset-locations", IoTDataMapper.toResponseDTO(savedData));

    if (batteryLevel != null && batteryLevel < 10.0) {
      log.warn("Low battery for asset {}: {}%", asset.getId(), batteryLevel);
      // TODO: Create notification/alert here
    }

    return IoTDataMapper.toResponseDTO(savedData);
  }

  @Override
  public void startSimulator() {
    if (simulatorRunning.compareAndSet(false, true)) {
      simulatorFuture = taskScheduler.scheduleAtFixedRate(this::runSimulationStep, Duration.ofSeconds(10));
      log.info("IoT Simulator started. Pushing data every 10 seconds.");
    }
  }

  @Override
  public void stopSimulator() {
    if (simulatorRunning.compareAndSet(true, false)) {
      if (simulatorFuture != null) {
        simulatorFuture.cancel(false);
      }
      log.info("IoT Simulator stopped");
    }
  }

  private void runSimulationStep() {
    try {
      List<Asset> assets = assetRepository.findAll();
      if (assets.isEmpty()) {
        log.warn("Simulator running, but no assets found in the database to simulate.");
        return;
      }

      // Simulate data for a random asset
      Asset assetToSimulate = assets.get((int) (Math.random() * assets.size()));

      // Simulate realistic data points
      double temp = 20 + Math.random() * 15; // Temp between 20-35 C
      double battery = 5 + Math.random() * 95; // Battery between 5-100%
      boolean inUse = Math.random() > 0.5;

      // Simulate location data around Bengaluru
      // Bengaluru coordinates: ~12.97° N, 77.59° E
      double latitude = 12.97 + (Math.random() - 0.5) * 0.1; // Small random variation
      double longitude = 77.59 + (Math.random() - 0.5) * 0.1; // Small random variation

      log.info("Simulating data for asset '{}': Temp={}, Battery={}, Lat={}, Lon={}",
              assetToSimulate.getName(),
              String.format("%.2f", temp),
              String.format("%.2f", battery),
              String.format("%.4f", latitude),
              String.format("%.4f", longitude));

      processSensorData(assetToSimulate.getId(), temp, battery, inUse, latitude, longitude);

    } catch (Exception e) {
      log.error("Simulator step failed", e);
    }
  }
}