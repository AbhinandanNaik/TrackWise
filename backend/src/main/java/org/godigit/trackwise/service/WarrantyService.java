package org.godigit.trackwise.service;

import org.godigit.trackwise.model.Warranty;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WarrantyService {
  Warranty createOrUpdate(Warranty warranty);
  List<Warranty> findExpiringBetween(LocalDate from, LocalDate to);
  void extendWarranty(UUID warrantyId, LocalDate newEndDate);
}
