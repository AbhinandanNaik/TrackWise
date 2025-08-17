package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.MaintenanceResponseDTO;
import org.godigit.trackwise.model.MaintenanceLog;

public class MaintenanceMapper {

    public static MaintenanceResponseDTO toResponseDTO(MaintenanceLog log) {
        if (log == null) {
            return null;
        }

        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();
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