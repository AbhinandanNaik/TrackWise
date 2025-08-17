package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class IoTDataRequestDTO {
    private UUID assetId;
    private Double temperature;
    private Double batteryLevel;
    private Boolean inUse;
}