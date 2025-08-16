package org.godigit.trackwise.service;

import org.godigit.trackwise.model.IoTData;

import java.util.UUID;

public interface IoTService {
  IoTData ingest(IoTData data); // store a single reading
  void processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse); // convenience
  void startSimulator(); // optional: kick off background simulator (enabled in dev)
  void stopSimulator();
}
