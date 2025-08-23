package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.WarrantyRequest;
import org.godigit.trackwise.dto.WarrantyResponse;
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

/**
 * Service implementation for handling all business logic related to asset warranties.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class WarrantyServiceImpl implements WarrantyService {

  // Dependencies injected by the constructor.
  private final WarrantyRepository warrantyRepository;
  private final AssetRepository assetRepository;

  /**
   * Creates a new warranty or updates an existing one for a specific asset.
   * @param request The DTO containing the warranty details.
   * @return A DTO representing the created or updated warranty.
   */
  @Override
  public WarrantyResponse createOrUpdate(WarrantyRequest request) {
    // Find the asset to associate the warranty with.
    Asset asset = assetRepository.findById(request.getAssetId())
            .orElseThrow(() -> new NotFoundException("Asset not found: " + request.getAssetId()));

    // Check if the asset already has a warranty to update, otherwise create a new one.
    Warranty warranty = asset.getWarranty() != null ? asset.getWarranty() : new Warranty();

    // Populate the warranty entity with data from the request DTO.
    warranty.setAsset(asset);
    warranty.setStartDate(request.getStartDate());
    warranty.setEndDate(request.getEndDate());
    warranty.setVendor(request.getVendor());

    // Save the warranty to the database.
    Warranty saved = warrantyRepository.save(warranty);

    // Convert the saved entity to a DTO for the API response.
    return WarrantyMapper.toResponseDTO(saved);
  }

  /**
   * Finds all warranties that expire within a given date range.
   * @param from The start date of the search period.
   * @param to The end date of the search period.
   * @return A list of warranty DTOs expiring in the specified range.
   */
  @Override
  @Transactional(readOnly = true) // A performance optimization for read-only queries.
  public List<WarrantyResponse> findExpiringBetween(LocalDate from, LocalDate to) {
    // Call the repository method to find the warranties.
    return warrantyRepository.findByEndDateBetween(from, to)
            .stream() // Convert the list to a stream for processing.
            .map(WarrantyMapper::toResponseDTO) // Map each warranty entity to its DTO representation.
            .collect(Collectors.toList()); // Collect the results back into a list.
  }

  /**
   * Extends the end date of an existing warranty.
   * @param warrantyId The UUID of the warranty to extend.
   * @param newEndDate The new expiry date for the warranty.
   * @return A DTO representing the updated warranty.
   */
  @Override
  public WarrantyResponse extendWarranty(UUID warrantyId, LocalDate newEndDate) {
    // Find the existing warranty by its ID.
    Warranty w = warrantyRepository.findById(warrantyId)
            .orElseThrow(() -> new NotFoundException("Warranty not found: " + warrantyId));

    // Update the end date.
    w.setEndDate(newEndDate);

    // Save the changes to the database.
    Warranty saved = warrantyRepository.save(w);

    // Return the updated DTO.
    return WarrantyMapper.toResponseDTO(saved);
  }
}