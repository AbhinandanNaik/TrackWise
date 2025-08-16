package org.godigit.trackwise.mapper;

import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.dto.AssetResponseDTO;

public class AssetMapper {
    public static AssetResponseDTO toDTO(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setId(asset.getId());
        dto.setName(asset.getName());
        dto.setCategoryName(asset.getCategory() != null ? asset.getCategory().getName() : null);
        dto.setStatus(asset.getStatus() != null ? asset.getStatus().name() : null);
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setWarrantyExpiryDate(asset.getWarrantyExpiryDate());
        dto.setAssignedToName(asset.getAssignedTo() != null
                ? asset.getAssignedTo().getFirstName() + " " + asset.getAssignedTo().getLastName()
                : null);
        return dto;
    }
}
