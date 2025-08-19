package org.godigit.trackwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.dto.IoTDataRequestDTO;
import org.godigit.trackwise.dto.IoTDataResponseDTO;
import org.godigit.trackwise.service.IoTService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IoTController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class IoTControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IoTService iotService;

    @Autowired
    private ObjectMapper objectMapper;

    private IoTDataRequestDTO sampleRequest() {
        IoTDataRequestDTO request = new IoTDataRequestDTO();
        request.setAssetId(UUID.randomUUID());
        request.setTemperature(25.5);
        request.setBatteryLevel(85.0);
        request.setInUse(true);
        request.setLatitude(40.7128);
        request.setLongitude(-74.0060);
        return request;
    }

    private IoTDataResponseDTO sampleResponse() {
        IoTDataResponseDTO response = new IoTDataResponseDTO();
        response.setLogId(UUID.randomUUID());
        response.setAssetId(UUID.randomUUID());
        response.setAssetName("Asset Name");
        response.setTemperature(25.5);
        response.setBatteryLevel(85.0);
        response.setInUse(true);
        response.setTimestamp(Instant.now());
        return response;
    }

    @Test
    void shouldIngestIoTData() throws Exception {
        IoTDataRequestDTO request = sampleRequest();
        IoTDataResponseDTO response = sampleResponse();

        when(iotService.ingest(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/iot/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.temperature").value(25.5))
                .andExpect(jsonPath("$.batteryLevel").value(85.0));
    }

    @Test
    void shouldProcessSensorData() throws Exception {
        UUID assetId = UUID.randomUUID();
        IoTDataResponseDTO response = sampleResponse();
        response.setAssetId(assetId);

        when(iotService.processSensorData(
                eq(assetId),
                eq(25.5),
                eq(85.0),
                eq(true),
                eq(40.7128),
                eq(-74.0060)
        )).thenReturn(response);

        mockMvc.perform(post("/api/iot/process/{assetId}", assetId)
                        .param("temperature", "25.5")
                        .param("batteryLevel", "85.0")
                        .param("inUse", "true")
                        .param("latitude", "40.7128")
                        .param("longitude", "-74.0060"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetId").value(assetId.toString()));
    }

    @Test
    void shouldStartSimulator() throws Exception {
        doNothing().when(iotService).startSimulator();

        mockMvc.perform(post("/api/iot/simulator/start"))
                .andExpect(status().isOk());

        verify(iotService).startSimulator();
    }

    @Test
    void shouldStopSimulator() throws Exception {
        doNothing().when(iotService).stopSimulator();

        mockMvc.perform(post("/api/iot/simulator/stop"))
                .andExpect(status().isOk());

        verify(iotService).stopSimulator();
    }
}