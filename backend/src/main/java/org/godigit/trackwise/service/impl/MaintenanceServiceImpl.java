package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.godigit.trackwise.service.MaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

  private final MaintenanceLogRepository maintenanceLogRepository;
  private final AssetRepository assetRepository;

  @Override
  public MaintenanceLog addMaintenance(UUID assetId, MaintenanceLog log) {
    Asset asset = assetRepository.findById(assetId)
      .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));
    log.setAsset(asset);
    MaintenanceLog saved = maintenanceLogRepository.save(log);

    asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
    assetRepository.save(asset);

    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MaintenanceLog> listByAsset(UUID assetId) {
    return maintenanceLogRepository.findByAssetId(assetId);
  }
}
