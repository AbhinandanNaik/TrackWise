package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.WarrantyRepository; // Import WarrantyRepository
import org.godigit.trackwise.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for generating downloadable business reports.
 * This class is solely responsible for creating report data in byte formats (e.g., CSV).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // All methods are read-only, which is a performance optimization.
public class ReportServiceImpl implements ReportService {

  // Dependencies injected by the constructor.
  private final AssetRepository assetRepository;
  private final WarrantyRepository warrantyRepository; // Use this for warranty data

  /**
   * Generates a CSV report of warranties expiring within a given date range.
   * @param from The start date of the reporting period.
   * @param to The end date of the reporting period.
   * @return A byte array representing the CSV file content.
   */
  @Override
  public byte[] generateWarrantyExpiryReport(LocalDate from, LocalDate to) {
    // CORRECTED: Fetch from the WarrantyRepository, the single source of truth.
    List<Warranty> warranties = warrantyRepository.findByEndDateBetween(from, to);

    // Use a StringBuilder to efficiently build the CSV string.
    StringBuilder sb = new StringBuilder();

    // Add the header row to the CSV.
    sb.append("asset_id,asset_name,serial_number,warranty_end_date,vendor\n");

    // Loop through each warranty and append a new row to the CSV.
    warranties.forEach(w -> sb.append(w.getAsset().getId()).append(",")
            .append(w.getAsset().getName()).append(",")
            .append(w.getAsset().getSerialNumber()).append(",")
            .append(w.getEndDate()).append(",")
            .append(w.getVendor()).append("\n")
    );

    // Convert the final string to a byte array for the HTTP response.
    return sb.toString().getBytes();
  }

  /**
   * Generates a CSV report of all assets older than a specified number of days.
   * @param olderThanDays The age threshold in days based on the purchase date.
   * @return A byte array representing the CSV file content.
   */
  @Override
  public byte[] generateAssetAgingReport(int olderThanDays) {
    // CORRECTED: Calculate the cutoff date based on the dynamic 'olderThanDays' parameter.
    LocalDate cutoffDate = LocalDate.now().minusDays(olderThanDays);

    // Call the new, dynamic repository method.
    List<Asset> assets = assetRepository.findByPurchaseDateBefore(cutoffDate);

    // Build the CSV string.
    StringBuilder sb = new StringBuilder();
    sb.append("asset_id,asset_name,purchase_date\n");
    assets.forEach(a -> sb.append(a.getId()).append(",")
            .append(a.getName()).append(",")
            .append(a.getPurchaseDate()).append("\n")
    );

    return sb.toString().getBytes();
  }
}