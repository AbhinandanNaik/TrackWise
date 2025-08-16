package org.godigit.trackwise.service;

import org.godigit.trackwise.model.CheckInOutLog;

import java.util.List;
import java.util.UUID;

public interface CheckInOutService {
  CheckInOutLog checkoutAsset(UUID assetId, UUID employeeId);
  CheckInOutLog checkinAsset(UUID assetId, UUID employeeId);
  List<CheckInOutLog> historyByAsset(UUID assetId);
  List<CheckInOutLog> historyByEmployee(UUID employeeId);
}
