package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.IoTDataRequestDTO;
import org.godigit.trackwise.dto.IoTDataResponseDTO;

import java.util.UUID;

public interface IoTService {
  IoTDataResponseDTO ingest(IoTDataRequestDTO request);
  IoTDataResponseDTO processSensorData(UUID assetId, Double temperature, Double batteryLevel, Boolean inUse);
  void startSimulator();
  void stopSimulator();
}