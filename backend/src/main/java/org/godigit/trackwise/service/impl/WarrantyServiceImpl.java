package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.repository.WarrantyRepository;
import org.godigit.trackwise.service.WarrantyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarrantyServiceImpl implements WarrantyService {

  private final WarrantyRepository warrantyRepository;

  @Override
  public Warranty createOrUpdate(Warranty warranty) {
    return warrantyRepository.save(warranty);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Warranty> findExpiringBetween(LocalDate from, LocalDate to) {
    return warrantyRepository.findByExpiryDateBetween(from, to);
  }

  @Override
  public void extendWarranty(UUID warrantyId, LocalDate newEndDate) {
    Warranty w = warrantyRepository.findById(warrantyId)
      .orElseThrow(() -> new NotFoundException("Warranty not found: " + warrantyId));
    w.setEndDate(newEndDate);
    warrantyRepository.save(w);
  }
}
