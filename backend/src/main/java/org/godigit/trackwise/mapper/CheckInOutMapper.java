package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.CheckInOutResponse;
import org.godigit.trackwise.model.CheckInOutLog;

public class CheckInOutMapper {

    public static CheckInOutResponse toDTO(CheckInOutLog log) {
        CheckInOutResponse dto = new CheckInOutResponse();
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
