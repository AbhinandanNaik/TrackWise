package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AssetScanRequest {
    private UUID assetId;
    private UUID employeeId;
}