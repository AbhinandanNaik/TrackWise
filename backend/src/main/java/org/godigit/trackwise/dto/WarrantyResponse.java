package org.godigit.trackwise.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WarrantyResponse {
    private UUID warrantyId;
    private UUID assetId;
    private String assetName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String vendor;
}