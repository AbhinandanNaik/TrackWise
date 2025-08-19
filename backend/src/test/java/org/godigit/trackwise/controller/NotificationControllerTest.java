package org.godigit.trackwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.dto.EmailRequestDTO;
import org.godigit.trackwise.dto.NotificationRequestDTO;
import org.godigit.trackwise.dto.NotificationResponseDTO;
import org.godigit.trackwise.service.NotificationService;
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

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationRequestDTO sampleNotificationRequest() {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setRecipientId(UUID.randomUUID());
        request.setMessage("Your asset requires maintenance");
        return request;
    }

    private NotificationResponseDTO sampleNotificationResponse() {
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(UUID.randomUUID());
        response.setRecipientId(UUID.randomUUID());
        response.setRecipientName("John Doe");
        response.setMessage("Your asset requires maintenance");
        response.setCreatedAt(Instant.now());
        response.setRead(false);
        return response;
    }

    private EmailRequestDTO sampleEmailRequest() {
        EmailRequestDTO request = new EmailRequestDTO();
        request.setTo("user@example.com");
        request.setSubject("Asset Maintenance Reminder");
        request.setBody("Your asset is due for maintenance.");
        return request;
    }

    @Test
    void shouldCreateInAppNotification() throws Exception {
        NotificationRequestDTO request = sampleNotificationRequest();
        NotificationResponseDTO response = sampleNotificationResponse();

        when(notificationService.createInAppNotification(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/notifications/in-app")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientName").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Your asset requires maintenance"));
    }

    @Test
    void shouldSendEmail() throws Exception {
        EmailRequestDTO request = sampleEmailRequest();

        doNothing().when(notificationService).sendEmail(Mockito.any());

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(notificationService).sendEmail(Mockito.any());
    }
}