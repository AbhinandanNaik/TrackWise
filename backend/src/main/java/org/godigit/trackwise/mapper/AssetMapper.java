package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetCategory;
import org.godigit.trackwise.model.AssetStatus;
import org.godigit.trackwise.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public Asset toEntity(AssetRequest request, AssetCategory category, Employee employee) {
        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setCategory(category);
        asset.setStatus(request.getStatus() != null ?
                Enum.valueOf(AssetStatus.class, request.getStatus()) : null);
        asset.setAssignedTo(employee);
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setSerialNumber(request.getSerialNumber());
        return asset;
    }

    public void updateEntity(Asset asset, AssetRequest request, AssetCategory category, Employee employee) {
        asset.setName(request.getName());
        asset.setCategory(category);
        asset.setStatus(request.getStatus() != null ?
                Enum.valueOf(AssetStatus.class, request.getStatus()) : null);
        asset.setAssignedTo(employee);
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setSerialNumber(request.getSerialNumber());
    }

    public AssetResponse toResponse(Asset asset) {
        AssetResponse dto = new AssetResponse();
        dto.setId(asset.getId());
        dto.setName(asset.getName());
        dto.setCategoryName(asset.getCategory() != null ? asset.getCategory().getName() : null);
        dto.setStatus(asset.getStatus() != null ? asset.getStatus().name() : null);
        dto.setAssignedEmployee(asset.getAssignedTo() != null ? asset.getAssignedTo().getFirstName() : null);
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setWarrantyExpiryDate(asset.getWarranty() != null ? asset.getWarranty().getEndDate() : null);
        return dto;
    }
}
