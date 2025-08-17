package org.godigit.trackwise.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AssetResponse {
    private UUID id;
    private String name;
    private String categoryName;
    private String status;
    private String assignedEmployee;
    private LocalDate purchaseDate;
    private String serialNumber;
    private LocalDate warrantyExpiryDate;
}
