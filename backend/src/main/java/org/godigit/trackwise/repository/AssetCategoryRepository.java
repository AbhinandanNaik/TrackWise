package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {
}
