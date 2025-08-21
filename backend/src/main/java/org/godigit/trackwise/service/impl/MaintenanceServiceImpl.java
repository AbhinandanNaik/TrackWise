package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.MaintenanceMapper;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.godigit.trackwise.service.MaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

  private final MaintenanceLogRepository maintenanceLogRepository;
  private final AssetRepository assetRepository;

  @Override
  public MaintenanceResponse addMaintenance(UUID assetId, MaintenanceRequest request) {
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    MaintenanceLog log = new MaintenanceLog();
    log.setAsset(asset);
    log.setDescription(request.getDescription());
    log.setMaintenanceDate(request.getMaintenanceDate());
    log.setPerformedBy(request.getPerformedBy());

    MaintenanceLog saved = maintenanceLogRepository.save(log);

    asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
    assetRepository.save(asset);

    return MaintenanceMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MaintenanceResponse> listByAsset(UUID assetId) {
    return maintenanceLogRepository.findByAssetId(assetId)
            .stream()
            .map(MaintenanceMapper::toResponseDTO)
            .collect(Collectors.toList());
  }
}