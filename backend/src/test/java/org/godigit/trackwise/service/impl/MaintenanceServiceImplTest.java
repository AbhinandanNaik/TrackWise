package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.MaintenanceRequest;
import org.godigit.trackwise.dto.MaintenanceResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.Enum.AssetStatus;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
public class MaintenanceServiceImplTest {

    @Mock
    private MaintenanceLogRepository maintenanceLogRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private MaintenanceServiceImpl maintenanceService;

    private Asset testAsset;
    private MaintenanceLog testMaintenanceLog;
    private MaintenanceRequest testRequestDTO;
    private UUID assetId;

    @BeforeEach
    void setUp() {
        assetId = UUID.randomUUID();
        
        // Setup test asset
        testAsset = new Asset();
        testAsset.setId(assetId);
        testAsset.setName("Test Asset");
        testAsset.setStatus(AssetStatus.AVAILABLE);
        
        // Setup test maintenance log
        testMaintenanceLog = new MaintenanceLog();
        testMaintenanceLog.setId(UUID.randomUUID());
        testMaintenanceLog.setAsset(testAsset);
        testMaintenanceLog.setDescription("Regular maintenance");
        testMaintenanceLog.setMaintenanceDate(LocalDate.now());
        testMaintenanceLog.setPerformedBy("John Technician");
        
        // Setup test request DTO
        testRequestDTO = new MaintenanceRequest();
        testRequestDTO.setDescription("Regular maintenance");
        testRequestDTO.setMaintenanceDate(LocalDate.now());
        testRequestDTO.setPerformedBy("John Technician");
    }

    @Test
    void addMaintenance_ShouldCreateMaintenanceLogAndUpdateAssetStatus() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(maintenanceLogRepository.save(any(MaintenanceLog.class))).thenReturn(testMaintenanceLog);
        
        // Act
        MaintenanceResponse response = maintenanceService.addMaintenance(assetId, testRequestDTO);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLogId()).isEqualTo(testMaintenanceLog.getId());
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getAssetName()).isEqualTo("Test Asset");
        assertThat(response.getDescription()).isEqualTo("Regular maintenance");
        assertThat(response.getMaintenanceDate()).isEqualTo(LocalDate.now());
        assertThat(response.getPerformedBy()).isEqualTo("John Technician");
        
        // Verify asset status is updated
        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository).save(assetCaptor.capture());
        assertThat(assetCaptor.getValue().getStatus()).isEqualTo(AssetStatus.UNDER_MAINTENANCE);
        
        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(maintenanceLogRepository).save(any(MaintenanceLog.class));
    }

    @Test
    void addMaintenance_ShouldThrowNotFoundException_WhenAssetNotFound() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> maintenanceService.addMaintenance(assetId, testRequestDTO));
        
        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(maintenanceLogRepository, never()).save(any(MaintenanceLog.class));
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void listByAsset_ShouldReturnMaintenanceLogs() {
        // Arrange
        MaintenanceLog log1 = new MaintenanceLog();
        log1.setId(UUID.randomUUID());
        log1.setAsset(testAsset);
        log1.setDescription("Regular maintenance");
        log1.setMaintenanceDate(LocalDate.now().minusDays(30));
        log1.setPerformedBy("John Technician");
        
        MaintenanceLog log2 = new MaintenanceLog();
        log2.setId(UUID.randomUUID());
        log2.setAsset(testAsset);
        log2.setDescription("Emergency repair");
        log2.setMaintenanceDate(LocalDate.now().minusDays(15));
        log2.setPerformedBy("Jane Engineer");
        
        List<MaintenanceLog> logs = Arrays.asList(log1, log2);
        
        when(maintenanceLogRepository.findByAssetId(assetId)).thenReturn(logs);
        
        // Act
        List<MaintenanceResponse> responses = maintenanceService.listByAsset(assetId);
        
        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getDescription()).isEqualTo("Regular maintenance");
        assertThat(responses.get(0).getPerformedBy()).isEqualTo("John Technician");
        assertThat(responses.get(1).getDescription()).isEqualTo("Emergency repair");
        assertThat(responses.get(1).getPerformedBy()).isEqualTo("Jane Engineer");
        
        // Verify interactions
        verify(maintenanceLogRepository).findByAssetId(assetId);
    }

    @Test
    void listByAsset_ShouldReturnEmptyList_WhenNoLogsFound() {
        // Arrange
        when(maintenanceLogRepository.findByAssetId(assetId)).thenReturn(List.of());
        
        // Act
        List<MaintenanceResponse> responses = maintenanceService.listByAsset(assetId);
        
        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
        
        // Verify interactions
        verify(maintenanceLogRepository).findByAssetId(assetId);
    }
}