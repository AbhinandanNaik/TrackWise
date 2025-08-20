package org.godigit.trackwise.controller;

import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void shouldGenerateWarrantyExpiryReport() throws Exception {
        String from = "2024-01-01";
        String to = "2024-12-31";
        byte[] reportData = "asset_id,name,warranty_expiry_date\n123,Laptop,2024-06-30".getBytes();

        when(reportService.generateWarrantyExpiryReport(
                eq(LocalDate.parse(from)),
                eq(LocalDate.parse(to))))
                .thenReturn(reportData);

        mockMvc.perform(get("/api/reports/warranty-expiry")
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=warranty_expiry_report.csv"))
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().bytes(reportData));
    }

    @Test
    void shouldGenerateAssetAgingReport() throws Exception {
        int olderThanDays = 365;
        byte[] reportData = "asset_id,name,purchase_date,age_in_days\n123,Laptop,2023-01-01,400".getBytes();

        when(reportService.generateAssetAgingReport(eq(olderThanDays)))
                .thenReturn(reportData);

        mockMvc.perform(get("/api/reports/asset-aging")
                        .param("olderThanDays", String.valueOf(olderThanDays)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=asset_aging_report.csv"))
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().bytes(reportData));
    }
}