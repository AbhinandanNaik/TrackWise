package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.AssetScanRequestDTO;
import org.godigit.trackwise.dto.CheckInOutRequestDTO;
import org.godigit.trackwise.dto.CheckInOutResponseDTO;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.CheckInOutMapper;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.CheckInOutLogRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckInOutServiceImplTest {

    @Mock
    private CheckInOutLogRepository checkInOutLogRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CheckInOutServiceImpl checkInOutService;

    private UUID assetId;
    private UUID employeeId;
    private Asset asset;
    private Employee employee;
    private CheckInOutLog checkInOutLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        assetId = UUID.randomUUID();
        employeeId = UUID.randomUUID();

        asset = new Asset();
        asset.setId(assetId);
        asset.setName("Laptop");
        asset.setStatus(AssetStatus.AVAILABLE);

        employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        checkInOutLog = new CheckInOutLog();
        checkInOutLog.setId(UUID.randomUUID());
        checkInOutLog.setAsset(asset);
        checkInOutLog.setEmployee(employee);
        checkInOutLog.setCheckOutTime(Instant.now());
        checkInOutLog.setAction(CheckInOutAction.CHECK_OUT);
    }

    @Test
    void shouldCheckoutAsset() {
        // Arrange
        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);
        // Remove setNotes as it doesn't exist in CheckInOutRequestDTO

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(checkInOutLogRepository.save(any(CheckInOutLog.class))).thenReturn(checkInOutLog);

        // Act
        CheckInOutResponseDTO response = checkInOutService.checkoutAsset(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getEmployeeId()).isEqualTo(employeeId);
        // Remove getAction assertion as it doesn't exist in CheckInOutResponseDTO

        verify(assetRepository).findById(assetId);
        verify(employeeRepository).findById(employeeId);
        verify(checkInOutLogRepository).save(any(CheckInOutLog.class));
        verify(assetRepository).save(asset);
    }

    @Test
    void shouldThrowExceptionWhenAssetNotAvailableForCheckout() {
        // Arrange
        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        asset.setStatus(AssetStatus.ASSIGNED);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act & Assert
        assertThatThrownBy(() -> checkInOutService.checkoutAsset(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Asset is not available for checkout");

        verify(assetRepository).findById(assetId);
        verify(employeeRepository).findById(employeeId);
        verify(checkInOutLogRepository, never()).save(any());
    }

    @Test
    void shouldCheckinAsset() {
        // Arrange
        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);
        // Remove setNotes as it doesn't exist in CheckInOutRequestDTO

        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedTo(employee);

        CheckInOutLog checkinLog = new CheckInOutLog();
        checkinLog.setId(UUID.randomUUID());
        checkinLog.setAsset(asset);
        checkinLog.setEmployee(employee);
        checkinLog.setCheckInTime(Instant.now());
        checkinLog.setAction(CheckInOutAction.CHECK_IN);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(checkInOutLogRepository.save(any(CheckInOutLog.class))).thenReturn(checkinLog);

        // Act
        CheckInOutResponseDTO response = checkInOutService.checkinAsset(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getEmployeeId()).isEqualTo(employeeId);
        // Remove getAction assertion as it doesn't exist in CheckInOutResponseDTO

        verify(assetRepository).findById(assetId);
        verify(employeeRepository).findById(employeeId);
        verify(checkInOutLogRepository).save(any(CheckInOutLog.class));
        verify(assetRepository).save(asset);
    }

    @Test
    void shouldGetHistoryByAsset() {
        // Arrange
        when(checkInOutLogRepository.findByAssetId(assetId))
                .thenReturn(List.of(checkInOutLog));

        // Act
        List<CheckInOutResponseDTO> history = checkInOutService.historyByAsset(assetId);

        // Assert
        assertThat(history).isNotNull();
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getAssetId()).isEqualTo(assetId);

        verify(checkInOutLogRepository).findByAssetId(assetId);
    }

    @Test
    void shouldGetHistoryByEmployee() {
        // Arrange
        when(checkInOutLogRepository.findByEmployeeId(employeeId))
                .thenReturn(List.of(checkInOutLog));

        // Act
        List<CheckInOutResponseDTO> history = checkInOutService.historyByEmployee(employeeId);

        // Assert
        assertThat(history).isNotNull();
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getEmployeeId()).isEqualTo(employeeId);

        verify(checkInOutLogRepository).findByEmployeeId(employeeId);
    }

    @Test
    void shouldProcessAssetScan() {
        // Arrange
        AssetScanRequestDTO request = new AssetScanRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);
        // Remove setLatitude and setLongitude as they don't exist in AssetScanRequestDTO

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(checkInOutLogRepository.save(any(CheckInOutLog.class))).thenReturn(checkInOutLog);

        // Act
        CheckInOutResponseDTO response = checkInOutService.processAssetScan(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);

        verify(assetRepository).findById(assetId);
        verify(employeeRepository).findById(employeeId);
        verify(checkInOutLogRepository).save(any(CheckInOutLog.class));
    }
}