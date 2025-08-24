package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MaintenanceRequest {
    private String description;
    private LocalDate maintenanceDate;
    private String performedBy;
}