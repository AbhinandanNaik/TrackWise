package org.godigit.trackwise.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetRequestDTO {
    private String name;
    private UUID categoryId;        // reference to AssetCategory
    private LocalDate purchaseDate;
    private String serialNumber;
    private UUID assignedToId;      // optional, for assigning employee
    private LocalDate warrantyExpiryDate;
}
