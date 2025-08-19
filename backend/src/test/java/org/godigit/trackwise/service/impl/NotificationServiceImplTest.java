package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.EmailRequestDTO;
import org.godigit.trackwise.dto.NotificationRequestDTO;
import org.godigit.trackwise.dto.NotificationResponseDTO;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.Notification;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Employee testEmployee;
    private Notification testNotification;
    private NotificationRequestDTO testNotificationRequestDTO;
    private EmailRequestDTO testEmailRequestDTO;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        
        // Setup test employee
        testEmployee = new Employee();
        testEmployee.setId(employeeId);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        
        // Setup test notification
        testNotification = Notification.builder()
                .id(UUID.randomUUID())
                .recipient(testEmployee)
                .message("Test notification message")
                .read(false)
                .createdAt(Instant.now())
                .build();
        
        // Setup test notification request DTO
        testNotificationRequestDTO = new NotificationRequestDTO();
        testNotificationRequestDTO.setRecipientId(employeeId);
        testNotificationRequestDTO.setMessage("Test notification message");
        
        // Setup test email request DTO
        testEmailRequestDTO = new EmailRequestDTO();
        testEmailRequestDTO.setTo("john.doe@example.com");
        testEmailRequestDTO.setSubject("Test Email Subject");
        testEmailRequestDTO.setBody("Test email body content");
    }

    @Test
    void createInAppNotification_ShouldCreateAndReturnNotification() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // Act
        NotificationResponseDTO response = notificationService.createInAppNotification(testNotificationRequestDTO);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testNotification.getId());
        assertThat(response.getRecipientId()).isEqualTo(employeeId);
        assertThat(response.getRecipientName()).isEqualTo("John Doe");
        assertThat(response.getMessage()).isEqualTo("Test notification message");
        assertThat(response.isRead()).isFalse();
        assertThat(response.getCreatedAt()).isNotNull();
        
        // Verify interactions
        verify(employeeRepository).findById(employeeId);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createInAppNotification_ShouldThrowNotFoundException_WhenEmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> 
                notificationService.createInAppNotification(testNotificationRequestDTO));
        
        // Verify interactions
        verify(employeeRepository).findById(employeeId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void sendEmail_ShouldSendEmailCorrectly() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // Act
        notificationService.sendEmail(testEmailRequestDTO);
        
        // Assert - Capture the SimpleMailMessage to verify its properties
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        
        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly("john.doe@example.com");
        assertThat(capturedMessage.getSubject()).isEqualTo("Test Email Subject");
        assertThat(capturedMessage.getText()).isEqualTo("Test email body content");
    }
}