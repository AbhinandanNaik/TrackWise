package org.godigit.trackwise.service;

import org.godigit.trackwise.model.MaintenanceLog;

import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
  MaintenanceLog addMaintenance(UUID assetId, MaintenanceLog log);
  List<MaintenanceLog> listByAsset(UUID assetId);
}
