package org.godigit.trackwise.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CheckInOutResponse {
    private UUID id;               // log id
    private UUID assetId;
    private String assetName;
    private UUID employeeId;
    private String employeeName;
    private Instant checkOutTime;
    private Instant checkInTime;
}
