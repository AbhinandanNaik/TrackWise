package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.IoTDataRequest;
import org.godigit.trackwise.dto.IoTDataResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.IoTData;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.IoTDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IoTServiceImplTest {

    @Mock
    private IoTDataRepository iotDataRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    @InjectMocks
    private IoTServiceImpl iotService;

    private Asset testAsset;
    private IoTData testIoTData;
    private IoTDataRequest testRequestDTO;
    private UUID assetId;

    @BeforeEach
    void setUp() {
        assetId = UUID.randomUUID();

        // Setup test asset
        testAsset = new Asset();
        testAsset.setId(assetId);
        testAsset.setName("Test Asset");

        // Setup test IoT data
        testIoTData = new IoTData();
        testIoTData.setId(UUID.randomUUID());
        testIoTData.setAsset(testAsset);
        testIoTData.setTemperature(25.5);
        testIoTData.setBatteryLevel(75.0);
        testIoTData.setInUse(true);
        testIoTData.setLatitude(12.9716);
        testIoTData.setLongitude(77.5946);
        testIoTData.setTimestamp(Instant.now());

        // Setup test request DTO
        testRequestDTO = new IoTDataRequest();
        testRequestDTO.setAssetId(assetId);
        testRequestDTO.setTemperature(25.5);
        testRequestDTO.setBatteryLevel(75.0);
        testRequestDTO.setInUse(true);
        testRequestDTO.setLatitude(12.9716);
        testRequestDTO.setLongitude(77.5946);
    }

    @Test
    void ingest_ShouldSaveIoTDataAndReturnResponse() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(iotDataRepository.save(any(IoTData.class))).thenReturn(testIoTData);

        // Act
        IoTDataResponse response = iotService.ingest(testRequestDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLogId()).isEqualTo(testIoTData.getId());
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getAssetName()).isEqualTo("Test Asset");
        assertThat(response.getTemperature()).isEqualTo(25.5);
        assertThat(response.getBatteryLevel()).isEqualTo(75.0);
        assertThat(response.getInUse()).isTrue();

        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(iotDataRepository).save(any(IoTData.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/asset-locations"), any(IoTDataResponse.class));
    }

    @Test
    void ingest_ShouldThrowNotFoundException_WhenAssetNotFound() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> iotService.ingest(testRequestDTO));

        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(iotDataRepository, never()).save(any(IoTData.class));
        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(), any(Map.class));
    }

    @Test
    void processSensorData_ShouldSaveIoTDataAndReturnResponse() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(iotDataRepository.save(any(IoTData.class))).thenReturn(testIoTData);

        // Act
        IoTDataResponse response = iotService.processSensorData(
                assetId, 25.5, 75.0, true, 12.9716, 77.5946);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getLogId()).isEqualTo(testIoTData.getId());
        assertThat(response.getAssetId()).isEqualTo(assetId);
        assertThat(response.getAssetName()).isEqualTo("Test Asset");
        assertThat(response.getTemperature()).isEqualTo(25.5);
        assertThat(response.getBatteryLevel()).isEqualTo(75.0);
        assertThat(response.getInUse()).isTrue();

        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(iotDataRepository).save(any(IoTData.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/asset-locations"), any(IoTDataResponse.class));
    }

    @Test
    void processSensorData_ShouldThrowNotFoundException_WhenAssetNotFound() {
        // Arrange
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> iotService.processSensorData(
                assetId, 25.5, 75.0, true, 12.9716, 77.5946));

        // Verify interactions
        verify(assetRepository).findById(assetId);
        verify(iotDataRepository, never()).save(any(IoTData.class));
        verify(messagingTemplate, never()).convertAndSend(any(String.class), any(), any(Map.class));
    }

    @Test
    void startSimulator_ShouldScheduleSimulationTask() {
        // Arrange
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));

        // Act
        iotService.startSimulator();

        // Assert
        verify(taskScheduler).scheduleAtFixedRate(any(Runnable.class), eq(Duration.ofSeconds(10)));
    }

    @Test
    void startSimulator_ShouldNotScheduleAgain_WhenAlreadyRunning() {
        // Arrange
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));

        // Act - Start simulator twice
        iotService.startSimulator();
        iotService.startSimulator(); // Second call should be ignored

        // Assert - Scheduler should only be called once
        verify(taskScheduler, times(1)).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
    }

    @Test
    void stopSimulator_ShouldCancelScheduledTask() {
        // Arrange - Start the simulator first
        doReturn(scheduledFuture).when(taskScheduler).scheduleAtFixedRate(any(Runnable.class), any(Duration.class));
        iotService.startSimulator();

        // Act
        iotService.stopSimulator();

        // Assert
        verify(scheduledFuture).cancel(false);
    }

    @Test
    void stopSimulator_ShouldDoNothing_WhenNotRunning() {
        // Act - Stop without starting
        iotService.stopSimulator();

        // Assert - No interactions with scheduledFuture
        verifyNoInteractions(scheduledFuture);
    }
}