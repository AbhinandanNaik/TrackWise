package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.WarrantyRequest;
import org.godigit.trackwise.dto.WarrantyResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WarrantyService {
  WarrantyResponse createOrUpdate(WarrantyRequest request);
  List<WarrantyResponse> findExpiringBetween(LocalDate from, LocalDate to);
  WarrantyResponse extendWarranty(UUID warrantyId, LocalDate newEndDate);
}