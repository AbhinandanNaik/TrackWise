package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Asset asset1;
    private Asset asset2;
    private LocalDate from;
    private LocalDate to;

    @BeforeEach
    void setUp() {
        // Setup test dates
        from = LocalDate.now().plusMonths(1);
        to = LocalDate.now().plusMonths(3);
        
        // Setup test assets
        asset1 = new Asset();
        asset1.setId(UUID.randomUUID());
        asset1.setName("Laptop XPS 15");
        asset1.setWarrantyExpiryDate(from.plusDays(10));
        asset1.setPurchaseDate(LocalDate.now().minusYears(2));
        
        asset2 = new Asset();
        asset2.setId(UUID.randomUUID());
        asset2.setName("Monitor Dell U2720Q");
        asset2.setWarrantyExpiryDate(from.plusDays(20));
        asset2.setPurchaseDate(LocalDate.now().minusYears(1).minusMonths(6));
    }

    @Test
    void generateWarrantyExpiryReport_ShouldReturnReportBytes() {
        // Arrange
        List<Asset> assets = Arrays.asList(asset1, asset2);
        when(assetRepository.findByWarrantyExpiryDateBetween(from, to)).thenReturn(assets);
        
        // Act
        byte[] reportBytes = reportService.generateWarrantyExpiryReport(from, to);
        
        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);
        
        String report = new String(reportBytes);
        assertThat(report).contains("id,name,warranty_end");
        assertThat(report).contains(asset1.getId().toString());
        assertThat(report).contains(asset1.getName());
        assertThat(report).contains(asset1.getWarrantyExpiryDate().toString());
        assertThat(report).contains(asset2.getId().toString());
        assertThat(report).contains(asset2.getName());
        assertThat(report).contains(asset2.getWarrantyExpiryDate().toString());
        
        // Verify interactions
        verify(assetRepository).findByWarrantyExpiryDateBetween(from, to);
    }

    @Test
    void generateWarrantyExpiryReport_ShouldReturnEmptyReport_WhenNoAssetsFound() {
        // Arrange
        when(assetRepository.findByWarrantyExpiryDateBetween(from, to)).thenReturn(List.of());
        
        // Act
        byte[] reportBytes = reportService.generateWarrantyExpiryReport(from, to);
        
        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);
        
        String report = new String(reportBytes);
        assertThat(report).contains("id,name,warranty_end");
        assertThat(report).doesNotContain(asset1.getId().toString());
        assertThat(report).doesNotContain(asset2.getId().toString());
        
        // Verify interactions
        verify(assetRepository).findByWarrantyExpiryDateBetween(from, to);
    }

    @Test
    void generateAssetAgingReport_ShouldReturnReportBytes() {
        // Arrange
        List<Asset> assets = Arrays.asList(asset1, asset2);
        when(assetRepository.findAssetsOlderThanOneYear()).thenReturn(assets);
        
        // Act
        byte[] reportBytes = reportService.generateAssetAgingReport(365);
        
        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);
        
        String report = new String(reportBytes);
        assertThat(report).contains("id,name,purchaseDate");
        assertThat(report).contains(asset1.getId().toString());
        assertThat(report).contains(asset1.getName());
        assertThat(report).contains(asset1.getPurchaseDate().toString());
        assertThat(report).contains(asset2.getId().toString());
        assertThat(report).contains(asset2.getName());
        assertThat(report).contains(asset2.getPurchaseDate().toString());
        
        // Verify interactions
        verify(assetRepository).findAssetsOlderThanOneYear();
    }

    @Test
    void generateAssetAgingReport_ShouldReturnEmptyReport_WhenNoAssetsFound() {
        // Arrange
        when(assetRepository.findAssetsOlderThanOneYear()).thenReturn(List.of());
        
        // Act
        byte[] reportBytes = reportService.generateAssetAgingReport(365);
        
        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);
        
        String report = new String(reportBytes);
        assertThat(report).contains("id,name,purchaseDate");
        assertThat(report).doesNotContain(asset1.getId().toString());
        assertThat(report).doesNotContain(asset2.getId().toString());
        
        // Verify interactions
        verify(assetRepository).findAssetsOlderThanOneYear();
    }
}