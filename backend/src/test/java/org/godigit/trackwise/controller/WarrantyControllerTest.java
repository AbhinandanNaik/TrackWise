package org.godigit.trackwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.dto.WarrantyRequest;
import org.godigit.trackwise.dto.WarrantyResponse;
import org.godigit.trackwise.service.WarrantyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WarrantyController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class WarrantyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WarrantyService warrantyService;

    @Autowired
    private ObjectMapper objectMapper;

    private WarrantyRequest sampleRequest() {
        WarrantyRequest request = new WarrantyRequest();
        request.setAssetId(UUID.randomUUID());
        request.setStartDate(LocalDate.of(2024, 1, 1));
        request.setEndDate(LocalDate.of(2026, 1, 1));
        request.setVendor("Warranty Provider Inc.");
        return request;
    }

    private WarrantyResponse sampleResponse() {
        WarrantyResponse response = new WarrantyResponse();
        response.setWarrantyId(UUID.randomUUID());
        response.setAssetId(UUID.randomUUID());
        response.setStartDate(LocalDate.of(2024, 1, 1));
        response.setEndDate(LocalDate.of(2026, 1, 1));
        response.setVendor("Warranty Provider Inc.");
        response.setAssetName("Test Asset");
        return response;
    }

    @Test
    void shouldCreateOrUpdateWarranty() throws Exception {
        WarrantyRequest request = sampleRequest();
        WarrantyResponse response = sampleResponse();

        when(warrantyService.createOrUpdate(any())).thenReturn(response);

        mockMvc.perform(post("/api/warranties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vendor").value("Warranty Provider Inc."));
    }

    @Test
    void shouldFindWarrantiesExpiringBetween() throws Exception {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        WarrantyResponse response = sampleResponse();

        when(warrantyService.findExpiringBetween(eq(from), eq(to)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/warranties/expiring")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vendor").value("Warranty Provider Inc."));
    }

    @Test
    void shouldExtendWarranty() throws Exception {
        UUID warrantyId = UUID.randomUUID();
        LocalDate newEndDate = LocalDate.of(2027, 1, 1);
        WarrantyResponse response = sampleResponse();
        response.setWarrantyId(warrantyId);
        response.setEndDate(newEndDate);

        when(warrantyService.extendWarranty(eq(warrantyId), eq(newEndDate)))
                .thenReturn(response);

        mockMvc.perform(put("/api/warranties/{id}/extend", warrantyId)
                        .param("newEndDate", newEndDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warrantyId").value(warrantyId.toString()))
                .andExpect(jsonPath("$.endDate").value(newEndDate.toString()));
    }
}