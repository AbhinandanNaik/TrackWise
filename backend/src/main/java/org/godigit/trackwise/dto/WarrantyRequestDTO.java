package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WarrantyRequestDTO {
    private UUID assetId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String vendor;
}