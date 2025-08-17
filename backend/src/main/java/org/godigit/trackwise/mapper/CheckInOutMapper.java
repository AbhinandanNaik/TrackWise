package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.CheckInOutResponseDTO;
import org.godigit.trackwise.model.CheckInOutLog;

public class CheckInOutMapper {

    public static CheckInOutResponseDTO toDTO(CheckInOutLog log) {
        CheckInOutResponseDTO dto = new CheckInOutResponseDTO();
        dto.setId(log.getId());
        dto.setAssetId(log.getAsset().getId());
        dto.setAssetName(log.getAsset().getName()); // assumes Asset has `name`
        dto.setEmployeeId(log.getEmployee().getId());
        dto.setEmployeeName(
                log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName()
        );
        dto.setCheckOutTime(log.getCheckOutTime());
        dto.setCheckInTime(log.getCheckInTime());
        return dto;
    }
}
