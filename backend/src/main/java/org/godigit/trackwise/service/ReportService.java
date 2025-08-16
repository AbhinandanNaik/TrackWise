package org.godigit.trackwise.service;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
  /**
   * Generate an asset aging/warranty report as PDF bytes.
   */
  byte[] generateWarrantyExpiryReport(LocalDate from, LocalDate to);

  byte[] generateAssetAgingReport(int olderThanDays);
}

