package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.MaintenanceResponse;
import org.godigit.trackwise.model.MaintenanceLog;

public class MaintenanceMapper {

    public static MaintenanceResponse toResponseDTO(MaintenanceLog log) {
        if (log == null) {
            return null;
        }

        MaintenanceResponse dto = new MaintenanceResponse();
        dto.setLogId(log.getId());
        dto.setDescription(log.getDescription());
        dto.setMaintenanceDate(log.getMaintenanceDate());
        dto.setPerformedBy(log.getPerformedBy());

        if (log.getAsset() != null) {
            dto.setAssetId(log.getAsset().getId());
            dto.setAssetName(log.getAsset().getName());
        }

        return dto;
    }
}