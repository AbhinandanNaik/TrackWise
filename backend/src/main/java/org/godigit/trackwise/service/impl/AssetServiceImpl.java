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
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing the full lifecycle and related operations of assets.
 * This class contains the business logic for creating, retrieving, updating, deleting,
 * assigning, and searching for assets.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

  private final AssetRepository assetRepository;
  private final AssetCategoryRepository categoryRepository;
  private final EmployeeRepository employeeRepository;
  private final WarrantyRepository warrantyRepository;
  private final AssetMapper assetMapper;

  /**
   * Creates a new asset and its associated warranty based on the request data.
   * @param request The DTO containing the details for the new asset.
   * @return A DTO representing the newly created asset.
   */
  @Override
  public AssetResponse create(AssetRequest request) {
    // Find the category for the asset, throwing an exception if not found.
    AssetCategory category = (request.getCategoryId() != null)
            ? categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"))
            : null;

    // Find the employee to assign the asset to (this is optional).
    Employee employee = (request.getEmployeeId() != null)
            ? employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new NotFoundException("Employee not found"))
            : null;

    // Use the mapper to create a new Asset entity from the request DTO.
    Asset asset = assetMapper.toEntity(request, category, employee);

    // Set a default status of AVAILABLE if none was provided.
    if (asset.getStatus() == null) {
      asset.setStatus(AssetStatus.AVAILABLE);
    }

    // Save the asset to the database.
    Asset saved = assetRepository.save(asset);

    // If a warranty date is provided, create and save a new warranty record.
    if (request.getWarrantyExpiryDate() != null) {
      Warranty warranty = new Warranty();
      warranty.setAsset(saved);
      warranty.setEndDate(request.getWarrantyExpiryDate());
      // This saves the warranty separately from the asset.
      warrantyRepository.save(warranty);
      saved.setWarranty(warranty);
    }

    // Map the saved entity to a response DTO and return it.
    return assetMapper.toResponse(saved);
  }

  /**
   * Finds assets whose name contains a search term, with pagination.
   * @param name The search term.
   * @param pageable Pagination information.
   * @return A paginated list of asset DTOs.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<AssetResponse> findByName(String name, Pageable pageable) {
    // Find assets by name and map the resulting Page of entities to a Page of DTOs.
    return assetRepository.findByNameContainingIgnoreCase(name, pageable)
            .map(assetMapper::toResponse);
  }

  /**
   * Retrieves a single asset by its unique ID.
   * @param id The UUID of the asset.
   * @return A DTO representing the found asset.
   */
  @Override
  public AssetResponse getById(UUID id) {
    // Find the asset by its ID, or throw an exception if it doesn't exist.
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));
    // Map the entity to a DTO and return it.
    return assetMapper.toResponse(asset);
  }

  /**
   * Finds all assets with a specific status.
   * @param status The status to filter by.
   * @return A list of asset DTOs matching the status.
   */
  @Override
  @Transactional(readOnly = true)
  public List<AssetResponse> findByStatus(AssetStatus status) {
    // Find assets by status and map the resulting list of entities to a list of DTOs.
    return assetRepository.findByStatus(status)
            .stream().map(assetMapper::toResponse).toList();
  }

  /**
   * Retrieves a paginated list of all assets.
   * @param pageable Pagination information.
   * @return A paginated list of asset DTOs.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<AssetResponse> list(Pageable pageable) {
    // Find all assets and map the Page of entities to a Page of DTOs.
    return assetRepository.findAll(pageable).map(assetMapper::toResponse);
  }

  /**
   * Updates an existing asset with new information.
   * @param id The UUID of the asset to update.
   * @param request The DTO containing the new data.
   * @return A DTO representing the updated asset.
   */
  @Override
  public AssetResponse update(UUID id, AssetRequest request) {
    // First, fetch the existing asset from the database.
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));

    // Fetch related entities.
    AssetCategory category = (request.getCategoryId() != null)
            ? categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"))
            : null;

    Employee employee = (request.getEmployeeId() != null)
            ? employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new NotFoundException("Employee not found"))
            : null;

    // Use the mapper to update the entity's fields from the DTO.
    assetMapper.updateEntity(asset, request, category, employee);

    // If a warranty date is provided, update or create the warranty record.
    if (request.getWarrantyExpiryDate() != null) {
      Warranty warranty = asset.getWarranty();
      if (warranty == null) {
        warranty = new Warranty();
        warranty.setAsset(asset);
      }
      warranty.setEndDate(request.getWarrantyExpiryDate());
      // This saves the warranty separately.
      warrantyRepository.save(warranty);
      asset.setWarranty(warranty);
    }

    // Save the updated asset and return its DTO representation.
    return assetMapper.toResponse(assetRepository.save(asset));
  }

  /**
   * Deletes an asset from the database.
   * @param id The UUID of the asset to delete.
   */
  @Override
  public void delete(UUID id) {
    // Find the asset first to ensure it exists before attempting to delete.
    Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Asset not found"));
    assetRepository.delete(asset);
  }


}