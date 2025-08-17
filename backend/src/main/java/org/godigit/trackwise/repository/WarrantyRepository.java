package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WarrantyRepository extends JpaRepository<Warranty, UUID> {


  // In WarrantyRepository.java - Correct
  List<Warranty> findByEndDateBetween(LocalDate from, LocalDate to);
}
