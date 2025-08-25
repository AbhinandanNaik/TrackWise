//package org.godigit.trackwise.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.godigit.trackwise.config.SecurityConfig;
//import org.godigit.trackwise.dto.EmailRequest;
//import org.godigit.trackwise.dto.NotificationRequest;
//import org.godigit.trackwise.dto.NotificationResponse;
//import org.godigit.trackwise.service.NotificationService;
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
//import java.util.UUID;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(NotificationController.class)
//@Import(SecurityConfig.class)
//@AutoConfigureMockMvc(addFilters = false) // disables security filters
//class NotificationControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private NotificationService notificationService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private NotificationRequest sampleNotificationRequest() {
//        NotificationRequest request = new NotificationRequest();
//        request.setRecipientId(UUID.randomUUID());
//        request.setMessage("Your asset requires maintenance");
//        return request;
//    }
//
//    private NotificationResponse sampleNotificationResponse() {
//        NotificationResponse response = new NotificationResponse();
//        response.setId(UUID.randomUUID());
//        response.setRecipientId(UUID.randomUUID());
//        response.setRecipientName("John Doe");
//        response.setMessage("Your asset requires maintenance");
//        response.setCreatedAt(Instant.now());
//        response.setRead(false);
//        return response;
//    }
//
//    private EmailRequest sampleEmailRequest() {
//        EmailRequest request = new EmailRequest();
//        request.setTo("user@example.com");
//        request.setSubject("Asset Maintenance Reminder");
//        request.setBody("Your asset is due for maintenance.");
//        return request;
//    }
//
//    @Test
//    void shouldCreateInAppNotification() throws Exception {
//        NotificationRequest request = sampleNotificationRequest();
//        NotificationResponse response = sampleNotificationResponse();
//
//        when(notificationService.createInAppNotification(Mockito.any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/notifications/in-app")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.recipientName").value("John Doe"))
//                .andExpect(jsonPath("$.message").value("Your asset requires maintenance"));
//    }
//
//    @Test
//    void shouldSendEmail() throws Exception {
//        EmailRequest request = sampleEmailRequest();
//
//        doNothing().when(notificationService).sendEmail(Mockito.any());
//
//        mockMvc.perform(post("/api/notifications/email")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        verify(notificationService).sendEmail(Mockito.any());
//    }
//}