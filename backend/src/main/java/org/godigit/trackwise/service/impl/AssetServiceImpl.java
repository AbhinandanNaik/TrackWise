package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.AssetMapper;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.repository.*;
import org.godigit.trackwise.service.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

  private final AssetRepository assetRepository;
  private final AssetCategoryRepository categoryRepository;
  private final EmployeeRepository employeeRepository;
  private final WarrantyRepository warrantyRepository;
  private final AssetMapper assetMapper;

  @Override
  public AssetResponse create(AssetRequest request) {
    AssetCategory category = (request.getCategoryId() != null)
            ? categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"))
            : null;

    Employee employee = (request.getEmployeeId() != null)
            ? employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new NotFoundException("Employee not found"))
            : null;

    Asset asset = assetMapper.toEntity(request, category, employee);

    if (asset.getStatus() == null) {
      asset.setStatus(AssetStatus.AVAILABLE);
    }

    Asset saved = assetRepository.save(asset);

    if (request.getWarrantyExpiryDate() != null) {
      Warranty warranty = new Warranty();
      warranty.setAsset(saved);
      warranty.setEndDate(request.getWarrantyExpiryDate());
      warrantyRepository.save(warranty);
      saved.setWarranty(warranty);
    }

    return assetMapper.toResponse(saved);
  }

  @Override
  public AssetResponse getById(UUID id) {
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));
    return assetMapper.toResponse(asset);
  }

  @Override
  public Page<AssetResponse> list(Pageable pageable) {
    return assetRepository.findAll(pageable).map(assetMapper::toResponse);
  }

  @Override
  public AssetResponse update(UUID id, AssetRequest request) {
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));

    AssetCategory category = (request.getCategoryId() != null)
            ? categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"))
            : null;

    Employee employee = (request.getEmployeeId() != null)
            ? employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new NotFoundException("Employee not found"))
            : null;

    assetMapper.updateEntity(asset, request, category, employee);

    if (request.getWarrantyExpiryDate() != null) {
      Warranty warranty = asset.getWarranty();
      if (warranty == null) {
        warranty = new Warranty();
        warranty.setAsset(asset);
      }
      warranty.setEndDate(request.getWarrantyExpiryDate());
      warrantyRepository.save(warranty);
      asset.setWarranty(warranty);
    }

    return assetMapper.toResponse(assetRepository.save(asset));
  }

  @Override
  public void delete(UUID id) {
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));
    assetRepository.delete(asset);
  }

  @Override
  public AssetResponse assignToEmployee(UUID assetId, UUID employeeId) {
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found"));
    Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NotFoundException("Employee not found"));

    asset.setAssignedTo(employee);
    return assetMapper.toResponse(assetRepository.save(asset));
  }

  @Override
  public AssetResponse unassign(UUID assetId) {
    Asset asset = assetRepository.findById(assetId)
            .orElseThrow(() -> new NotFoundException("Asset not found"));

    asset.setAssignedTo(null); // Unassign employee
    asset.setStatus(AssetStatus.AVAILABLE); // Update status to AVAILABLE
    asset.setUpdatedAt(Instant.now()); // Optional: update timestamp

    return assetMapper.toResponse(assetRepository.save(asset));
  }



  @Override
  public List<AssetResponse> findByStatus(AssetStatus status) {
    return assetRepository.findByStatus(status)
            .stream().map(assetMapper::toResponse).toList();
  }

  @Override
  public List<AssetResponse> findWithWarrantyExpiringBetween(LocalDate from, LocalDate to) {
    return warrantyRepository.findByEndDateBetween(from, to)
            .stream().map(Warranty::getAsset)
            .map(assetMapper::toResponse)
            .toList();
  }
}
