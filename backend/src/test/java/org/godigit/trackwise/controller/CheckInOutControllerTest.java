package org.godigit.trackwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.dto.AssetScanRequestDTO;
import org.godigit.trackwise.dto.CheckInOutRequestDTO;
import org.godigit.trackwise.dto.CheckInOutResponseDTO;
import org.godigit.trackwise.service.CheckInOutService;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckInOutController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckInOutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckInOutService checkInOutService;

    @Autowired
    private ObjectMapper objectMapper;

    private CheckInOutRequestDTO sampleRequest() {
        CheckInOutRequestDTO dto = new CheckInOutRequestDTO();
        dto.setAssetId(UUID.randomUUID());
        dto.setEmployeeId(UUID.randomUUID());
        return dto;
    }

    private CheckInOutResponseDTO sampleResponse(UUID logId) {
        CheckInOutResponseDTO dto = new CheckInOutResponseDTO();
        dto.setId(logId);
        dto.setAssetId(UUID.randomUUID());
        dto.setAssetName("Laptop");
        dto.setEmployeeId(UUID.randomUUID());
        dto.setEmployeeName("Raju Kumar");
        dto.setCheckOutTime(Instant.now());
        dto.setCheckInTime(null);
        return dto;
    }

    @Test
    void shouldCheckoutAsset() throws Exception {
        CheckInOutRequestDTO request = sampleRequest();
        CheckInOutResponseDTO response = sampleResponse(UUID.randomUUID());

        Mockito.when(checkInOutService.checkoutAsset(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/checkinout/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assetName").value("Laptop"))
                .andExpect(jsonPath("$.employeeName").value("Raju Kumar"));
    }

    @Test
    void shouldCheckinAsset() throws Exception {
        CheckInOutRequestDTO request = sampleRequest();
        CheckInOutResponseDTO response = sampleResponse(UUID.randomUUID());
        response.setCheckInTime(Instant.now());

        Mockito.when(checkInOutService.checkinAsset(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/checkinout/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkInTime").exists());
    }

    @Test
    void shouldReturnHistoryByAsset() throws Exception {
        UUID assetId = UUID.randomUUID();
        CheckInOutResponseDTO response = sampleResponse(UUID.randomUUID());

        Mockito.when(checkInOutService.historyByAsset(assetId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/checkinout/asset/{assetId}/history", assetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetName").value("Laptop"));
    }

    @Test
    void shouldReturnHistoryByEmployee() throws Exception {
        UUID employeeId = UUID.randomUUID();
        CheckInOutResponseDTO response = sampleResponse(UUID.randomUUID());

        Mockito.when(checkInOutService.historyByEmployee(employeeId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/checkinout/employee/{employeeId}/history", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeName").value("Raju Kumar"));
    }

    @Test
    void shouldProcessScan() throws Exception {
        AssetScanRequestDTO request = new AssetScanRequestDTO();
        request.setAssetId(UUID.randomUUID());
        request.setEmployeeId(UUID.randomUUID());

        CheckInOutResponseDTO response = sampleResponse(UUID.randomUUID());

        Mockito.when(checkInOutService.processAssetScan(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/checkinout/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetName").value("Laptop"));
    }
}
