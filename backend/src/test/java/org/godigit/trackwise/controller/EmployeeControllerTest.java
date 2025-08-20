package org.godigit.trackwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.godigit.trackwise.config.SecurityConfig;
import org.godigit.trackwise.dto.EmployeeRequestDTO;
import org.godigit.trackwise.dto.EmployeeResponseDTO;
import org.godigit.trackwise.mapper.EmployeeMapper;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequestDTO sampleRequest() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setDepartmentId(UUID.randomUUID());
        return request;
    }

    private Employee sampleEmployee() {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        return employee;
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeRequestDTO request = sampleRequest();
        Employee employee = sampleEmployee();
        EmployeeResponseDTO response = EmployeeMapper.toDto(employee);

        when(employeeService.create(Mockito.any())).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {
        UUID id = UUID.randomUUID();
        Employee employee = sampleEmployee();
        employee.setId(id);

        when(employeeService.getById(id)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void shouldListEmployees() throws Exception {
        Employee employee = sampleEmployee();
        PageImpl<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeService.list(Mockito.any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName").value("John"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeRequestDTO request = sampleRequest();
        Employee employee = sampleEmployee();
        employee.setId(id);

        when(employeeService.update(eq(id), Mockito.any())).thenReturn(employee);

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(employeeService).delete(id);

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNoContent());

        verify(employeeService).delete(id);
    }
}