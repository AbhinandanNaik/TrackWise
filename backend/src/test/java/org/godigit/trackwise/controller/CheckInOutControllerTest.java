//package org.godigit.trackwise.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.godigit.trackwise.config.SecurityConfig;
//import org.godigit.trackwise.dto.AssetScanRequest;
//import org.godigit.trackwise.dto.CheckInOutRequest;
//import org.godigit.trackwise.dto.CheckInOutResponse;
//import org.godigit.trackwise.service.CheckInOutService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CheckInOutController.class)
//@Import(SecurityConfig.class)
//@AutoConfigureMockMvc(addFilters = false)
//class CheckInOutControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CheckInOutService checkInOutService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private CheckInOutRequest sampleRequest() {
//        CheckInOutRequest dto = new CheckInOutRequest();
//        dto.setAssetId(UUID.randomUUID());
//        dto.setEmployeeId(UUID.randomUUID());
//        return dto;
//    }
//
//    private CheckInOutResponse sampleResponse(UUID logId) {
//        CheckInOutResponse dto = new CheckInOutResponse();
//        dto.setId(logId);
//        dto.setAssetId(UUID.randomUUID());
//        dto.setAssetName("Laptop");
//        dto.setEmployeeId(UUID.randomUUID());
//        dto.setEmployeeName("Raju Kumar");
//        dto.setCheckOutTime(Instant.now());
//        dto.setCheckInTime(null);
//        return dto;
//    }
//
//    @Test
//    void shouldCheckoutAsset() throws Exception {
//        CheckInOutRequest request = sampleRequest();
//        CheckInOutResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(checkInOutService.checkoutAsset(Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/checkinout/checkout")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.assetName").value("Laptop"))
//                .andExpect(jsonPath("$.employeeName").value("Raju Kumar"));
//    }
//
//    @Test
//    void shouldCheckinAsset() throws Exception {
//        CheckInOutRequest request = sampleRequest();
//        CheckInOutResponse response = sampleResponse(UUID.randomUUID());
//        response.setCheckInTime(Instant.now());
//
//        Mockito.when(checkInOutService.checkinAsset(Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/checkinout/checkin")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.checkInTime").exists());
//    }
//
//    @Test
//    void shouldReturnHistoryByAsset() throws Exception {
//        UUID assetId = UUID.randomUUID();
//        CheckInOutResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(checkInOutService.historyByAsset(assetId)).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/api/checkinout/asset/{assetId}/history", assetId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].assetName").value("Laptop"));
//    }
//
//    @Test
//    void shouldReturnHistoryByEmployee() throws Exception {
//        UUID employeeId = UUID.randomUUID();
//        CheckInOutResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(checkInOutService.historyByEmployee(employeeId)).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/api/checkinout/employee/{employeeId}/history", employeeId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].employeeName").value("Raju Kumar"));
//    }
//
//    @Test
//    void shouldProcessScan() throws Exception {
//        AssetScanRequest request = new AssetScanRequest();
//        request.setAssetId(UUID.randomUUID());
//        request.setEmployeeId(UUID.randomUUID());
//
//        CheckInOutResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(checkInOutService.processAssetScan(Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/checkinout/scan")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.assetName").value("Laptop"));
//    }
//}
