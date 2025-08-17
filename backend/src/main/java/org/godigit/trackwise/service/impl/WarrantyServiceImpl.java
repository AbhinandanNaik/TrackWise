package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.WarrantyRequestDTO;
import org.godigit.trackwise.dto.WarrantyResponseDTO;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.WarrantyMapper;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.WarrantyRepository;
import org.godigit.trackwise.service.WarrantyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarrantyServiceImpl implements WarrantyService {

  private final WarrantyRepository warrantyRepository;
  private final AssetRepository assetRepository; // Needed to link the asset

  @Override
  public WarrantyResponseDTO createOrUpdate(WarrantyRequestDTO request) {
    Asset asset = assetRepository.findById(request.getAssetId())
            .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

    // Create a new warranty or find the existing one to update
    Warranty warranty = asset.getWarranty() != null ? asset.getWarranty() : new Warranty();

    warranty.setAsset(asset);
    warranty.setStartDate(request.getStartDate());
    warranty.setEndDate(request.getEndDate());
    warranty.setVendor(request.getVendor());

    Warranty saved = warrantyRepository.save(warranty);
    return WarrantyMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<WarrantyResponseDTO> findExpiringBetween(LocalDate from, LocalDate to) {
    return warrantyRepository.findByEndDateBetween(from, to)
            .stream()
            .map(WarrantyMapper::toResponseDTO)
            .collect(Collectors.toList());
  }

  @Override
  public WarrantyResponseDTO extendWarranty(UUID warrantyId, LocalDate newEndDate) {
    Warranty w = warrantyRepository.findById(warrantyId)
            .orElseThrow(() -> new NotFoundException("Warranty not found: " + warrantyId));
    w.setEndDate(newEndDate);
    Warranty saved = warrantyRepository.save(w);
    return WarrantyMapper.toResponseDTO(saved);
  }
}