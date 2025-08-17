package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.exception.NotFoundException;
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
  public IoTData ingest(IoTData data) {
    data.setTimestamp(Instant.now());
    return iotDataRepository.save(data);
  }

  @Override
  public void processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse) {
    Asset asset = assetRepository.findById(assetId)
      .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    IoTData d = new IoTData();
    d.setAsset(asset);
    d.setTemperature(temperature);
    d.setBatteryLevel(batteryLevel);
    d.setInUse(inUse);
    d.setTimestamp(Instant.now());
    iotDataRepository.save(d);

    // Example business logic: if battery < 10% create an alert (hook notification)
    if (batteryLevel != null && batteryLevel < 10.0) {
      log.warn("Low battery for asset {}: {}%", asset.getId(), batteryLevel);
      // create notification / alert (left as integration point)
    }
  }

  @Override
  public void startSimulator() {
    if (simulatorRunning.compareAndSet(false, true)) {
      // Correct: The time unit is now explicit and clear.
      simulatorFuture = taskScheduler.scheduleAtFixedRate(this::runSimulationStep, Duration.ofMillis(5000));
      log.info("IoT Simulator started");
    }
  }

  @Override
  public void stopSimulator() {
    if (simulatorRunning.compareAndSet(true, false)) {
      if (simulatorFuture != null) simulatorFuture.cancel(false);
      log.info("IoT Simulator stopped");
    }
  }

  private void runSimulationStep() {
    try {
      // example: pick first asset to simulate (for demo); production: iterate many devices
      assetRepository.findAll().stream().findFirst().ifPresent(asset -> {
        double temp = 20 + Math.random() * 15;
        double battery = 20 + Math.random() * 80;
        boolean inUse = Math.random() > 0.5;
        processSensorData(asset.getId(), temp, battery, inUse);
      });
    } catch (Exception e) {
      log.error("Simulator error", e);
    }
  }
}
