package org.godigit.trackwise.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AssetRequest {
    private String name;
    private UUID categoryId;
    private String status;   // ENUM as String for JSON flexibility
    private LocalDate warrantyExpiryDate;
    private UUID employeeId;
    private LocalDate purchaseDate;
    private String serialNumber;
}
