package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.AssetScanRequest;
import org.godigit.trackwise.dto.CheckInOutRequest; // Use DTOs
import org.godigit.trackwise.dto.CheckInOutResponse;

import java.util.List;
import java.util.UUID;

public interface CheckInOutService {

  // Use DTO for request to group parameters
  CheckInOutResponse checkoutAsset(CheckInOutRequest request);

  // Use DTO for request
  CheckInOutResponse checkinAsset(CheckInOutRequest request);

  List<CheckInOutResponse> historyByAsset(UUID assetId);

  List<CheckInOutResponse> historyByEmployee(UUID employeeId);

  // Add the new method for the smart scan
  CheckInOutResponse processAssetScan(AssetScanRequest request);
}