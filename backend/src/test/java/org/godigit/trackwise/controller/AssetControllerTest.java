//package org.godigit.trackwise.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.godigit.trackwise.config.SecurityConfig;
//import org.godigit.trackwise.dto.AssetRequest;
//import org.godigit.trackwise.dto.AssetResponse;
//import org.godigit.trackwise.service.AssetService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.UUID;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@WebMvcTest(AssetController.class)
//@Import(SecurityConfig.class)
//@AutoConfigureMockMvc(addFilters = false) // disables security filters
//class AssetControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AssetService assetService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private AssetRequest sampleRequest() {
//        AssetRequest request = new AssetRequest();
//        request.setName("Laptop");
//        request.setCategoryId(UUID.randomUUID());
//        request.setStatus("AVAILABLE");
//        request.setWarrantyExpiryDate(LocalDate.of(2026, 1, 1));
//        request.setEmployeeId(UUID.randomUUID());
//        request.setPurchaseDate(LocalDate.of(2024, 1, 1));
//        request.setSerialNumber("SN123456");
//        return request;
//    }
//
//    private AssetResponse sampleResponse(UUID id) {
//        AssetResponse response = new AssetResponse();
//        response.setId(id);
//        response.setName("Laptop");
//        response.setCategoryName("Electronics");
//        response.setStatus("AVAILABLE");
//        response.setAssignedEmployee("Raju");
//        response.setPurchaseDate(LocalDate.of(2024, 1, 1));
//        response.setSerialNumber("SN123456");
//        response.setWarrantyExpiryDate(LocalDate.of(2026, 1, 1));
//        return response;
//    }
//
//    @Test
//    void shouldCreateAsset() throws Exception {
//        AssetRequest request = sampleRequest();
//        AssetResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(assetService.create(Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/assets")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("Laptop"))
//                .andExpect(jsonPath("$.status").value("AVAILABLE"));
//    }
//
//    @Test
//    void shouldGetAssetById() throws Exception {
//        UUID id = UUID.randomUUID();
//        AssetResponse response = sampleResponse(id);
//
//        Mockito.when(assetService.getById(id)).thenReturn(response);
//
//        mockMvc.perform(get("/api/assets/{id}", id))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.name").value("Laptop"));
//    }
//
//    @Test
//    void shouldListAssets() throws Exception {
//        AssetResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(assetService.list(Mockito.any())).thenReturn(new PageImpl<>(List.of(response)));
//
//        mockMvc.perform(get("/api/assets?page=0&size=10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(1)))
//                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
//    }
//
//    @Test
//    void shouldUpdateAsset() throws Exception {
//        UUID id = UUID.randomUUID();
//        AssetRequest request = sampleRequest();
//        AssetResponse response = sampleResponse(id);
//
//        Mockito.when(assetService.update(Mockito.eq(id), Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(put("/api/assets/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id.toString()));
//    }
//
//    @Test
//    void shouldDeleteAsset() throws Exception {
//        UUID id = UUID.randomUUID();
//
//        mockMvc.perform(delete("/api/assets/{id}", id))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void shouldAssignAssetToEmployee() throws Exception {
//        UUID assetId = UUID.randomUUID();
//        UUID employeeId = UUID.randomUUID();
//        AssetResponse response = sampleResponse(assetId);
//
//        Mockito.when(assetService.assignToEmployee(assetId, employeeId)).thenReturn(response);
//
//        mockMvc.perform(post("/api/assets/{assetId}/assign/{employeeId}", assetId, employeeId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.assignedEmployee").value("Raju"));
//    }
//
//    @Test
//    void shouldUnassignAsset() throws Exception {
//        UUID assetId = UUID.randomUUID();
//        AssetResponse response = sampleResponse(assetId);
//        response.setAssignedEmployee(null);
//
//        Mockito.when(assetService.unassign(assetId)).thenReturn(response);
//
//        mockMvc.perform(post("/api/assets/{assetId}/unassign", assetId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.assignedEmployee").doesNotExist());
//    }
//
//    @Test
//    void shouldFindByStatus() throws Exception {
//        AssetResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(assetService.findByStatus(Mockito.any())).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/api/assets/status/AVAILABLE"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
//    }
//
//    @Test
//    void shouldFindWarrantyExpiringBetweenDates() throws Exception {
//        LocalDate from = LocalDate.of(2025, 1, 1);
//        LocalDate to = LocalDate.of(2026, 12, 31);
//        AssetResponse response = sampleResponse(UUID.randomUUID());
//
//        Mockito.when(assetService.findWithWarrantyExpiringBetween(from, to)).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/api/assets/warranty-expiring")
//                        .param("from", from.toString())
//                        .param("to", to.toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].warrantyExpiryDate").value("2026-01-01"));
//    }
//}
