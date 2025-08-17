package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.MaintenanceRequestDTO;
import org.godigit.trackwise.dto.MaintenanceResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
  MaintenanceResponseDTO addMaintenance(UUID assetId, MaintenanceRequestDTO request);
  List<MaintenanceResponseDTO> listByAsset(UUID assetId);
}