package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.IoTDataRequest;
import org.godigit.trackwise.dto.IoTDataResponse;

import java.util.UUID;

public interface IoTService {
  IoTDataResponse ingest(IoTDataRequest request);

  // Add latitude and longitude to the method signature
  IoTDataResponse processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse, Double latitude, Double longitude);

  void startSimulator();
  void stopSimulator();
}