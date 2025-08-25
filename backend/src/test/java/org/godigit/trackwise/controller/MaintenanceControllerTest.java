//package org.godigit.trackwise.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.godigit.trackwise.config.SecurityConfig;
//import org.godigit.trackwise.dto.MaintenanceRequest;
//import org.godigit.trackwise.dto.MaintenanceResponse;
//import org.godigit.trackwise.service.MaintenanceService;
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
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(MaintenanceController.class)
//@Import(SecurityConfig.class)
//@AutoConfigureMockMvc(addFilters = false) // disables security filters
//class MaintenanceControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MaintenanceService maintenanceService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MaintenanceRequest sampleRequest() {
//        MaintenanceRequest request = new MaintenanceRequest();
//        request.setDescription("Regular maintenance check");
//        request.setMaintenanceDate(LocalDate.now());
//        request.setPerformedBy("John Technician");
//        return request;
//    }
//
//    private MaintenanceResponse sampleResponse() {
//        MaintenanceResponse response = new MaintenanceResponse();
//        response.setLogId(UUID.randomUUID());
//        response.setAssetId(UUID.randomUUID());
//        response.setAssetName("Test Asset");
//        response.setDescription("Regular maintenance check");
//        response.setMaintenanceDate(LocalDate.now());
//        response.setPerformedBy("John Technician");
//        return response;
//    }
//
//    @Test
//    void shouldAddMaintenance() throws Exception {
//        UUID assetId = UUID.randomUUID();
//        MaintenanceRequest request = sampleRequest();
//        MaintenanceResponse response = sampleResponse();
//        response.setAssetId(assetId);
//
//        when(maintenanceService.addMaintenance(eq(assetId), Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/maintenance/{assetId}", assetId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.assetId").value(assetId.toString()))
//                .andExpect(jsonPath("$.description").value("Regular maintenance check"));
//    }
//
//    @Test
//    void shouldListMaintenanceByAsset() throws Exception {
//        UUID assetId = UUID.randomUUID();
//        MaintenanceResponse response = sampleResponse();
//        response.setAssetId(assetId);
//
//        when(maintenanceService.listByAsset(assetId)).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/api/maintenance/{assetId}", assetId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].assetId").value(assetId.toString()));
//    }
//}