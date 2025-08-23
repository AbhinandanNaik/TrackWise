package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.CheckInOutLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional; // Import Optional
import java.util.UUID;

public interface CheckInOutLogRepository extends JpaRepository<CheckInOutLog, UUID> {

  List<CheckInOutLog> findByAssetId(UUID assetId);

  List<CheckInOutLog> findByEmployeeId(UUID employeeId);

  // NEW METHOD: Finds the most recent checkout log for an asset that is still open (not checked in).
  Optional<CheckInOutLog> findFirstByAssetIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(UUID assetId);

  // Finds the most recent log entry that is still open (not checked in)
  Optional<CheckInOutLog> findFirstByEmployeeIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(UUID employeeId);

  // Finds all logs that were checked out before a certain date and have not been checked in yet
  List<CheckInOutLog> findByCheckOutTimeBeforeAndCheckInTimeIsNull(Instant since);
}