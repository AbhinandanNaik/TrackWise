package org.godigit.trackwise.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetResponseDTO {
    private UUID id;
    private String name;
    private String categoryName;
    private String status;
    private LocalDate purchaseDate;
    private String serialNumber;
    private LocalDate warrantyExpiryDate;
    private String assignedToName;  // Employee full name
}
