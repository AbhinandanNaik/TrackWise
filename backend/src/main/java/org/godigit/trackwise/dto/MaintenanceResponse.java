package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class MaintenanceResponse {
    private UUID logId;
    private UUID assetId;
    private String assetName;
    private String description;
    private LocalDate maintenanceDate;
    private String performedBy;
}