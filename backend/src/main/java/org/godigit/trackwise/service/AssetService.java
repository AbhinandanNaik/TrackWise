package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.AssetRequest;
import org.godigit.trackwise.dto.AssetResponse;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetService {
  AssetResponse create(AssetRequest request);
  AssetResponse getById(UUID id);
  Page<AssetResponse> list(Pageable pageable);
  AssetResponse update(UUID id, AssetRequest request);
  void delete(UUID id);
  List<AssetResponse> findByStatus(AssetStatus status);
  Page<AssetResponse> findByName(String name, Pageable pageable);
}
