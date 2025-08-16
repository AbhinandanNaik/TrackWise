package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

  private final AssetRepository assetRepository;

  @Override
  public byte[] generateWarrantyExpiryReport(LocalDate from, LocalDate to) {
    // Placeholder: produce a simple CSV/PDF byte[].
    // Integration point: use Apache POI or iText to produce real reports.
    var assets = assetRepository.findByWarrantyExpiryDateBetween(from, to);
    StringBuilder sb = new StringBuilder();
    sb.append("id,name,warranty_end\n");
    assets.forEach(a -> sb.append(a.getId()).append(",").append(a.getName()).append(",")
      .append(a.getWarrantyExpiryDate()).append("\n"));
    return sb.toString().getBytes();
  }

  @Override
  public byte[] generateAssetAgingReport(int olderThanDays) {
    var list = assetRepository.findAssetsOlderThanOneYear(); // example usage
    StringBuilder sb = new StringBuilder();
    sb.append("id,name,purchaseDate\n");
    list.forEach(a -> sb.append(a.getId()).append(",").append(a.getName()).append(",")
      .append(a.getPurchaseDate()).append("\n"));
    return sb.toString().getBytes();
  }
}
