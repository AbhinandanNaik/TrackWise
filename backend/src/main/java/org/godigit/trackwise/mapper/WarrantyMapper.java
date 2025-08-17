package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.WarrantyResponseDTO;
import org.godigit.trackwise.model.Warranty;

public class WarrantyMapper {

    public static WarrantyResponseDTO toResponseDTO(Warranty warranty) {
        if (warranty == null) {
            return null;
        }

        WarrantyResponseDTO dto = new WarrantyResponseDTO();
        dto.setWarrantyId(warranty.getId());
        dto.setStartDate(warranty.getStartDate());
        dto.setEndDate(warranty.getEndDate());
        dto.setVendor(warranty.getVendor());

        if (warranty.getAsset() != null) {
            dto.setAssetId(warranty.getAsset().getId());
            dto.setAssetName(warranty.getAsset().getName());
        }

        return dto;
    }
}