package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.MaintenanceMapper;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Enum.*;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.godigit.trackwise.service.MaintenanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for handling all business logic related to
 * asset maintenance logs.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class MaintenanceServiceImpl implements MaintenanceService {

  // Dependencies injected by the constructor.
  private final MaintenanceLogRepository maintenanceLogRepository;
  private final AssetRepository assetRepository;

  /**
   * Adds a new maintenance log for a specific asset and updates the asset's
   * status to UNDER_MAINTENANCE.
   *
   * @param assetId The UUID of the asset being serviced.
   * @param request The DTO containing the details of the maintenance work.
   * @return A DTO representing the newly created maintenance log.
   */
  @Override
  public MaintenanceResponse addMaintenance(UUID assetId, MaintenanceRequest request) {
    // Find the asset by its ID, or throw an error if it doesn't exist.
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found: " + assetId));

    // Create a new MaintenanceLog entity from the request DTO.
    MaintenanceLog log = new MaintenanceLog();
    log.setAsset(asset); // Link the log to the asset.
    log.setDescription(request.getDescription());
    log.setMaintenanceDate(request.getMaintenanceDate());
    log.setPerformedBy(request.getPerformedBy());

    // Save the new log entry to the database.
    MaintenanceLog saved = maintenanceLogRepository.save(log);

    // Update the asset's status to reflect that it's currently under maintenance.
    asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
    assetRepository.save(asset);

    // Convert the saved entity to a DTO to safely return to the client.
    return MaintenanceMapper.toResponseDTO(saved);
  }

  /**
   * Retrieves a list of all maintenance logs for a specific asset.
   *
   * @param assetId The UUID of the asset.
   * @return A list of maintenance log DTOs.
   */
  @Override
  @Transactional(readOnly = true) // This is a read-only operation, a performance optimization.
  public List<MaintenanceResponse> listByAsset(UUID assetId) {
    // Fetch all log entries for the given assetId from the database.
    return maintenanceLogRepository.findByAssetId(assetId)
            .stream() // Convert the list to a stream for processing.
            .map(MaintenanceMapper::toResponseDTO) // Map each log entity to its DTO representation.
            .collect(Collectors.toList()); // Collect the results back into a list.
  }


}