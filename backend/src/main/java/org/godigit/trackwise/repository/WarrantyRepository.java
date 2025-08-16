package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WarrantyRepository extends JpaRepository<Warranty, UUID> {

  List<Warranty> findByExpiryDateBefore(LocalDate date);

  List<Warranty> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
}
