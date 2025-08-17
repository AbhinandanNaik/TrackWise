package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, UUID> {

  List<MaintenanceLog> findByAssetId(UUID assetId);


}
