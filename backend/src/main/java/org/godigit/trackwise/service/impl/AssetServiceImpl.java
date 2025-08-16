package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.WarrantyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

  private final AssetRepository assetRepository;
  private final EmployeeRepository employeeRepository;
  private final WarrantyRepository warrantyRepository;

  @Override
  public Asset create(Asset asset) {
    if (asset.getStatus() == null) {
      asset.setStatus(AssetStatus.AVAILABLE);
    }
    Asset saved = assetRepository.save(asset);
    // persist warranty if present
    Warranty w = saved.getWarranty();
    if (w != null) {
      w.setAsset(saved);
      warrantyRepository.save(w);
    }
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Asset getById(UUID id) {
    return assetRepository.findById(id)
      .orElseThrow(() -> new NotFoundException("Asset not found: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Asset> list(Pageable pageable) {
    return assetRepository.findAll(pageable);
  }

  @Override
  public Asset update(UUID id, Asset updated) {
    Asset existing = getById(id);
    // update fields (selective)
    existing.setName(updated.getName());
    existing.setSerialNumber(updated.getSerialNumber());
    existing.setPurchaseDate(updated.getPurchaseDate());
    existing.setStatus(updated.getStatus());
    existing.setCategory(updated.getCategory());
    if (updated.getWarranty() != null) {
      Warranty w = existing.getWarranty();
      if (w == null) {
        w = updated.getWarranty();
        w.setAsset(existing);
        warrantyRepository.save(w);
      } else {
        w.setStartDate(updated.getWarranty().getStartDate());
        w.setEndDate(updated.getWarranty().getEndDate());
        w.setVendor(updated.getWarranty().getVendor());
        warrantyRepository.save(w);
      }
    }
    return assetRepository.save(existing);
  }

  @Override
  public void delete(UUID id) {
    Asset asset = getById(id);
    // soft delete pattern: mark inactive instead of DB delete
    asset.setStatus(AssetStatus.RETIRED);
    assetRepository.save(asset);
  }

  @Override
  public Asset assignToEmployee(UUID assetId, UUID employeeId) {
    Asset asset = getById(assetId);
    if (asset.getStatus() == AssetStatus.RETIRED || asset.getStatus() == AssetStatus.UNDER_MAINTENANCE) {
      throw new IllegalStateException("Asset cannot be assigned in its current state: " + asset.getStatus());
    }
    Employee emp = employeeRepository.findById(employeeId)
      .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));
    asset.setAssignedTo(emp);
    asset.setStatus(AssetStatus.ASSIGNED);
    return assetRepository.save(asset);
  }

  @Override
  public Asset unassign(UUID assetId) {
    Asset asset = getById(assetId);
    asset.setAssignedTo(null);
    asset.setStatus(AssetStatus.AVAILABLE);
    return assetRepository.save(asset);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Asset> findByStatus(AssetStatus status) {
    return assetRepository.findByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Asset> findWithWarrantyExpiringBetween(LocalDate from, LocalDate to) {
    return assetRepository.findByWarrantyExpiryDateBetween(from, to);
  }
}
