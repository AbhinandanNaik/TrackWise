package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.WarrantyRequestDTO;
import org.godigit.trackwise.dto.WarrantyResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WarrantyService {
  WarrantyResponseDTO createOrUpdate(WarrantyRequestDTO request);
  List<WarrantyResponseDTO> findExpiringBetween(LocalDate from, LocalDate to);
  WarrantyResponseDTO extendWarranty(UUID warrantyId, LocalDate newEndDate);
}