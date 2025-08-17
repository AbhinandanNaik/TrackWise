package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.AssetScanRequestDTO;
import org.godigit.trackwise.dto.CheckInOutRequestDTO; // Use DTOs
import org.godigit.trackwise.dto.CheckInOutResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CheckInOutService {

  // Use DTO for request to group parameters
  CheckInOutResponseDTO checkoutAsset(CheckInOutRequestDTO request);

  // Use DTO for request
  CheckInOutResponseDTO checkinAsset(CheckInOutRequestDTO request);

  List<CheckInOutResponseDTO> historyByAsset(UUID assetId);

  List<CheckInOutResponseDTO> historyByEmployee(UUID employeeId);

  // Add the new method for the smart scan
  CheckInOutResponseDTO processAssetScan(AssetScanRequestDTO request);
}