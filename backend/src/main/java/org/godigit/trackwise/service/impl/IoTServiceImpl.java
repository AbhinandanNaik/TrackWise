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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
    data.setTimestamp(Instant.now());

    IoTData savedData = iotDataRepository.save(data);

    // Example business logic: if battery < 10% create an alert (hook notification)
    if (request.getBatteryLevel() != null && request.getBatteryLevel() < 10.0) {
      log.warn("Low battery for asset {}: {}%", asset.getId(), request.getBatteryLevel());
      // TODO: Create notification/alert here
    }

    return IoTDataMapper.toResponseDTO(savedData);
  }

  @Override
  public IoTDataResponseDTO processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse) {
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    IoTData d = new IoTData();
    d.setAsset(asset);
    d.setTemperature(temperature);
    d.setBatteryLevel(batteryLevel);
    d.setInUse(inUse);
    d.setTimestamp(Instant.now());

    IoTData savedData = iotDataRepository.save(d);

    if (batteryLevel != null && batteryLevel < 10.0) {
      log.warn("Low battery for asset {}: {}%", asset.getId(), batteryLevel);
      // TODO: Create notification/alert here
    }

    // Map the saved entity to a DTO before returning
    return IoTDataMapper.toResponseDTO(savedData);
  }

  @Override
  public void startSimulator() {
    if (simulatorRunning.compareAndSet(false, true)) {
      simulatorFuture = taskScheduler.scheduleAtFixedRate(this::runSimulationStep, Duration.ofSeconds(10));
      log.info("IoT Simulator started");
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
      assetRepository.findAll().stream().findAny().ifPresent(asset -> {
        double temp = 20 + Math.random() * 15; // Simulate temperature between 20-35 C
        double battery = 5 + Math.random() * 95; // Simulate battery between 5-100%
        boolean inUse = Math.random() > 0.5;
        log.info("Simulating data for asset {}: Temp={}, Battery={}", asset.getName(), String.format("%.2f", temp), String.format("%.2f", battery));
        processSensorData(asset.getId(), temp, battery, inUse);
      });
    } catch (Exception e) {
      log.error("Simulator step failed", e);
    }
  }
}