package org.godigit.trackwise.service.impl;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private WarrantyRepository warrantyRepository;

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
        asset1.setSerialNumber("SN-LAP-001");
        asset1.setWarrantyExpiryDate(from.plusDays(10));
        asset1.setPurchaseDate(LocalDate.now().minusYears(2));

        asset2 = new Asset();
        asset2.setId(UUID.randomUUID());
        asset2.setName("Monitor Dell U2720Q");
        asset2.setSerialNumber("SN-MON-002");
        asset2.setWarrantyExpiryDate(from.plusDays(20));
        asset2.setPurchaseDate(LocalDate.now().minusYears(1).minusMonths(6));
    }

    @Test
    void generateWarrantyExpiryReport_ShouldReturnReportBytes() {
        // Arrange
        Warranty w1 = new Warranty();
        w1.setAsset(asset1);
        w1.setEndDate(asset1.getWarrantyExpiryDate());
        w1.setVendor("VendorA");

        Warranty w2 = new Warranty();
        w2.setAsset(asset2);
        w2.setEndDate(asset2.getWarrantyExpiryDate());
        w2.setVendor("VendorB");

        List<Warranty> warranties = Arrays.asList(w1, w2);
        when(warrantyRepository.findByEndDateBetween(from, to)).thenReturn(warranties);

        // Act
        byte[] reportBytes = reportService.generateWarrantyExpiryReport(from, to);

        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);

        String report = new String(reportBytes);
        // header according to implementation
        assertThat(report).contains("asset_id,asset_name,serial_number,warranty_end_date,vendor");
        // check content rows include expected asset data
        assertThat(report).contains(asset1.getId().toString());
        assertThat(report).contains(asset1.getName());
        assertThat(report).contains(asset1.getSerialNumber());
        assertThat(report).contains(asset1.getWarrantyExpiryDate().toString());
        assertThat(report).contains("VendorA");

        assertThat(report).contains(asset2.getId().toString());
        assertThat(report).contains(asset2.getName());
        assertThat(report).contains(asset2.getSerialNumber());
        assertThat(report).contains(asset2.getWarrantyExpiryDate().toString());
        assertThat(report).contains("VendorB");

        // Verify interactions
        verify(warrantyRepository).findByEndDateBetween(from, to);
    }

    @Test
    void generateWarrantyExpiryReport_ShouldReturnEmptyReport_WhenNoAssetsFound() {
        // Arrange
        when(warrantyRepository.findByEndDateBetween(from, to)).thenReturn(List.of());

        // Act
        byte[] reportBytes = reportService.generateWarrantyExpiryReport(from, to);

        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);

        String report = new String(reportBytes);
        assertThat(report).contains("asset_id,asset_name,serial_number,warranty_end_date,vendor");
        assertThat(report).doesNotContain(asset1.getId().toString());
        assertThat(report).doesNotContain(asset2.getId().toString());

        // Verify interactions
        verify(warrantyRepository).findByEndDateBetween(from, to);
    }

    @Test
    void generateAssetAgingReport_ShouldReturnReportBytes() {
        // Arrange
        List<Asset> assets = Arrays.asList(asset1, asset2);
        when(assetRepository.findByPurchaseDateBefore(any(LocalDate.class))).thenReturn(assets);

        // Act
        byte[] reportBytes = reportService.generateAssetAgingReport(365);

        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);

        String report = new String(reportBytes);
        // header according to implementation
        assertThat(report).contains("asset_id,asset_name,purchase_date");
        // check content rows include expected asset data
        assertThat(report).contains(asset1.getId().toString());
        assertThat(report).contains(asset1.getName());
        assertThat(report).contains(asset1.getPurchaseDate().toString());

        assertThat(report).contains(asset2.getId().toString());
        assertThat(report).contains(asset2.getName());
        assertThat(report).contains(asset2.getPurchaseDate().toString());

        // Verify interactions
        verify(assetRepository).findByPurchaseDateBefore(any(LocalDate.class));
    }

    @Test
    void generateAssetAgingReport_ShouldReturnEmptyReport_WhenNoAssetsFound() {
        // Arrange
        when(assetRepository.findByPurchaseDateBefore(any(LocalDate.class))).thenReturn(List.of());

        // Act
        byte[] reportBytes = reportService.generateAssetAgingReport(365);

        // Assert
        assertThat(reportBytes).isNotNull();
        assertThat(reportBytes.length).isGreaterThan(0);

        String report = new String(reportBytes);
        assertThat(report).contains("asset_id,asset_name,purchase_date");
        assertThat(report).doesNotContain(asset1.getId().toString());
        assertThat(report).doesNotContain(asset2.getId().toString());

        // Verify interactions
        verify(assetRepository).findByPurchaseDateBefore(any(LocalDate.class));
    }
}