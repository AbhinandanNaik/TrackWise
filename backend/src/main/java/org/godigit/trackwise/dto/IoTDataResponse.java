package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class IoTDataResponse {
    private UUID logId; // The ID of the IoTData record itself
    private UUID assetId;
    private String assetName;
    private Double temperature;
    private Double batteryLevel;
    private Boolean inUse;
    private Instant timestamp;
}