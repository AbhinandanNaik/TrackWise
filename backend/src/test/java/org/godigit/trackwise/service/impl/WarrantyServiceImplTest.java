package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.WarrantyRequestDTO;
import org.godigit.trackwise.dto.WarrantyResponseDTO;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Warranty;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.WarrantyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarrantyServiceImplTest {

    @Mock
    private WarrantyRepository warrantyRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private WarrantyServiceImpl warrantyService;

    private UUID assetId;
    private UUID warrantyId;
    private Asset asset;
    private Warranty warranty;
    private WarrantyRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        assetId = UUID.randomUUID();
        warrantyId = UUID.randomUUID();

        asset = new Asset();
        asset.setId(assetId);
        asset.setName("Test Asset");

        warranty = new Warranty();
        warranty.setId(warrantyId);
        warranty.setAsset(asset);
        warranty.setStartDate(LocalDate.of(2023, 1, 1));
        warranty.setEndDate(LocalDate.of(2024, 12, 31));
        warranty.setVendor("Test Vendor");

        asset.setWarranty(warranty);

        requestDTO = new WarrantyRequestDTO();
        requestDTO.setAssetId(assetId);
        requestDTO.setStartDate(LocalDate.of(2023, 1, 1));
        requestDTO.setEndDate(LocalDate.of(2024, 12, 31));
        requestDTO.setVendor("Test Vendor");
    }

    @Test
    void createOrUpdate_ShouldCreateNewWarranty_WhenAssetHasNoWarranty() {
        // Arrange
        Asset assetWithoutWarranty = new Asset();
        assetWithoutWarranty.setId(assetId);
        assetWithoutWarranty.setName("Test Asset");
        assetWithoutWarranty.setWarranty(null);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(assetWithoutWarranty));
        when(warrantyRepository.save(any(Warranty.class))).thenReturn(warranty);

        // Act
        WarrantyResponseDTO result = warrantyService.createOrUpdate(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWarrantyId()).isEqualTo(warrantyId);
        assertThat(result.getAssetId()).isEqualTo(assetId);
        assertThat(result.getAssetName()).isEqualTo("Test Asset");
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2023, 1, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(result.getVendor()).isEqualTo("Test Vendor");

        verify(assetRepository).findById(assetId);
        verify(warrantyRepository).save(any(Warranty.class));
    }

    @Test
    void createOrUpdate_ShouldUpdateExistingWarranty_WhenAssetHasWarranty() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(warrantyRepository.save(any(Warranty.class))).thenReturn(warranty);

        // Act
        WarrantyResponseDTO result = warrantyService.createOrUpdate(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWarrantyId()).isEqualTo(warrantyId);
        assertThat(result.getAssetId()).isEqualTo(assetId);
        assertThat(result.getVendor()).isEqualTo("Test Vendor");

        verify(assetRepository).findById(assetId);
        verify(warrantyRepository).save(any(Warranty.class));
    }

    @Test
    void createOrUpdate_ShouldThrowNotFoundException_WhenAssetNotFound() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> warrantyService.createOrUpdate(requestDTO));
        verify(assetRepository).findById(assetId);
        verify(warrantyRepository, never()).save(any(Warranty.class));
    }

    @Test
    void findExpiringBetween_ShouldReturnWarrantiesExpiringInDateRange() {
        // Arrange
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        
        Warranty warranty1 = new Warranty();
        warranty1.setId(UUID.randomUUID());
        warranty1.setAsset(asset);
        warranty1.setEndDate(LocalDate.of(2024, 6, 30));
        
        Warranty warranty2 = new Warranty();
        warranty2.setId(UUID.randomUUID());
        Asset asset2 = new Asset();
        asset2.setId(UUID.randomUUID());
        asset2.setName("Another Asset");
        warranty2.setAsset(asset2);
        warranty2.setEndDate(LocalDate.of(2024, 10, 15));
        
        List<Warranty> warranties = Arrays.asList(warranty1, warranty2);
        
        when(warrantyRepository.findByEndDateBetween(from, to)).thenReturn(warranties);
        
        // Act
        List<WarrantyResponseDTO> results = warrantyService.findExpiringBetween(from, to);
        
        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getAssetId()).isEqualTo(asset.getId());
        assertThat(results.get(1).getAssetId()).isEqualTo(asset2.getId());
        
        verify(warrantyRepository).findByEndDateBetween(from, to);
    }

    @Test
    void findExpiringBetween_ShouldReturnEmptyList_WhenNoWarrantiesExpireInRange() {
        // Arrange
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        
        when(warrantyRepository.findByEndDateBetween(from, to)).thenReturn(List.of());
        
        // Act
        List<WarrantyResponseDTO> results = warrantyService.findExpiringBetween(from, to);
        
        // Assert
        assertThat(results).isEmpty();
        verify(warrantyRepository).findByEndDateBetween(from, to);
    }

    @Test
    void extendWarranty_ShouldUpdateEndDate_WhenWarrantyExists() {
        // Arrange
        LocalDate newEndDate = LocalDate.of(2025, 12, 31);
        Warranty updatedWarranty = new Warranty();
        updatedWarranty.setId(warrantyId);
        updatedWarranty.setAsset(asset);
        updatedWarranty.setEndDate(newEndDate);
        
        when(warrantyRepository.findById(warrantyId)).thenReturn(Optional.of(warranty));
        when(warrantyRepository.save(any(Warranty.class))).thenReturn(updatedWarranty);
        
        // Act
        WarrantyResponseDTO result = warrantyService.extendWarranty(warrantyId, newEndDate);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWarrantyId()).isEqualTo(warrantyId);
        assertThat(result.getEndDate()).isEqualTo(newEndDate);
        
        verify(warrantyRepository).findById(warrantyId);
        verify(warrantyRepository).save(any(Warranty.class));
    }

    @Test
    void extendWarranty_ShouldThrowNotFoundException_WhenWarrantyNotFound() {
        // Arrange
        LocalDate newEndDate = LocalDate.of(2025, 12, 31);
        when(warrantyRepository.findById(warrantyId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> warrantyService.extendWarranty(warrantyId, newEndDate));
        
        verify(warrantyRepository).findById(warrantyId);
        verify(warrantyRepository, never()).save(any(Warranty.class));
    }
}