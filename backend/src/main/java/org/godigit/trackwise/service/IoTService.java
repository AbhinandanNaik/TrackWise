package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.IoTDataRequestDTO;
import org.godigit.trackwise.dto.IoTDataResponseDTO;

import java.util.UUID;

public interface IoTService {
  IoTDataResponseDTO ingest(IoTDataRequestDTO request);

  // Add latitude and longitude to the method signature
  IoTDataResponseDTO processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse, Double latitude, Double longitude);

  void startSimulator();
  void stopSimulator();
}