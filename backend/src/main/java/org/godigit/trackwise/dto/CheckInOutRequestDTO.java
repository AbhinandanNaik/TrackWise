package org.godigit.trackwise.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CheckInOutRequestDTO {
    private UUID assetId;
    private UUID employeeId;
}
