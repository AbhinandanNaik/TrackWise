package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.CheckInOutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CheckInOutLogRepository extends JpaRepository<CheckInOutLog, UUID> {

  List<CheckInOutLog> findByAssetId(UUID assetId);

  List<CheckInOutLog> findByEmployeeId(UUID employeeId);
}
