package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;

import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
  MaintenanceResponse addMaintenance(UUID assetId, MaintenanceRequest request);
  List<MaintenanceResponse> listByAsset(UUID assetId);
}