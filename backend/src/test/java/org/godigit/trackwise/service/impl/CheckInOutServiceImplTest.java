package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.AssetScanRequestDTO;
import org.godigit.trackwise.dto.CheckInOutRequestDTO;
import org.godigit.trackwise.dto.CheckInOutResponseDTO;
import org.godigit.trackwise.model.*;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.CheckInOutLogRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CheckInOutServiceImplTest {

    @InjectMocks
    private CheckInOutServiceImpl checkInOutService;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CheckInOutLogRepository checkInOutLogRepository;

    private UUID assetId;
    private UUID employeeId;
    private Asset asset;
    private Employee employee;
    private CheckInOutLog log;

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

        log = new CheckInOutLog();
        log.setId(UUID.randomUUID());
        log.setAsset(asset);
        log.setEmployee(employee);
        log.setCheckOutTime(Instant.now());
        log.setAction(CheckInOutAction.CHECK_OUT);
    }

    @Test
    void shouldCheckoutAsset() {
        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(checkInOutLogRepository.save(any())).thenReturn(log);

        CheckInOutResponseDTO response = checkInOutService.checkoutAsset(request);

        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getEmployeeId()).isEqualTo(employeeId);

        verify(assetRepository).save(asset);
    }

    @Test
    void shouldCheckinAsset() {
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedTo(employee);
        log.setCheckInTime(null);

        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        when(checkInOutLogRepository.findFirstByAssetIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(assetId))
                .thenReturn(Optional.of(log));
        when(checkInOutLogRepository.save(any())).thenReturn(log);

        CheckInOutResponseDTO response = checkInOutService.checkinAsset(request);

        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getEmployeeId()).isEqualTo(employeeId);

        verify(assetRepository).save(asset);
    }

    @Test
    void shouldProcessAssetScan_Checkout() {
        asset.setStatus(AssetStatus.AVAILABLE);

        AssetScanRequestDTO request = new AssetScanRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(checkInOutLogRepository.save(any())).thenReturn(log);

        CheckInOutResponseDTO response = checkInOutService.processAssetScan(request);

        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
    }

    @Test
    void shouldProcessAssetScan_Checkin() {
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedTo(employee);
        log.setCheckInTime(null);

        AssetScanRequestDTO request = new AssetScanRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(checkInOutLogRepository.findFirstByAssetIdAndCheckInTimeIsNullOrderByCheckOutTimeDesc(assetId))
                .thenReturn(Optional.of(log));
        when(checkInOutLogRepository.save(any())).thenReturn(log);

        CheckInOutResponseDTO response = checkInOutService.processAssetScan(request);

        assertThat(response).isNotNull();
        assertThat(response.getAssetId()).isEqualTo(assetId);
    }

    @Test
    void shouldReturnHistoryByAsset() {
        when(checkInOutLogRepository.findByAssetId(assetId)).thenReturn(List.of(log));

        List<CheckInOutResponseDTO> history = checkInOutService.historyByAsset(assetId);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getAssetId()).isEqualTo(assetId);
    }

    @Test
    void shouldReturnHistoryByEmployee() {
        when(checkInOutLogRepository.findByEmployeeId(employeeId)).thenReturn(List.of(log));

        List<CheckInOutResponseDTO> history = checkInOutService.historyByEmployee(employeeId);

        assertThat(history).hasSize(1);
        assertThat(history.get(0).getEmployeeId()).isEqualTo(employeeId);
    }

    @Test
    void shouldThrowIfAssetNotAvailableForCheckout() {
        asset.setStatus(AssetStatus.ASSIGNED);

        CheckInOutRequestDTO request = new CheckInOutRequestDTO();
        request.setAssetId(assetId);
        request.setEmployeeId(employeeId);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        assertThrows(IllegalStateException.class, () -> checkInOutService.checkoutAsset(request));
    }
}
