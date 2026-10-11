package com.example.customerservice;

import com.example.customerservice.controller.CustomerController;
import com.example.customerservice.dto.CustomerRequestDTO;
import com.example.customerservice.dto.CustomerResponseDTO;
import com.example.customerservice.dto.LoginRequestDTO;
import com.example.customerservice.exception.GlobalExceptionHandler;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/customers/99 - ID not found returns 404 and ApiResponseError format")
    void testGetCustomerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/customers/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Khách hàng với ID 99 không tồn tại!")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/customers/register - Register customer and return CustomerResponseDTO without password")
    void testRegisterCustomer() throws Exception {
        String jsonPayload = "{\"fullName\":\"Nguyen Van A\",\"email\":\"vana@example.com\",\"password\":\"secret123\"}";

        mockMvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fullName", is("Nguyen Van A")))
                .andExpect(jsonPath("$.email", is("vana@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/v1/customers/login - Login successful returns CustomerResponseDTO")
    void testLoginSuccess() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .fullName("Tran Van B")
                .email("vanb@example.com")
                .password("mypassword")
                .build();
        customerService.register(request);

        String loginPayload = "{\"email\":\"vanb@example.com\",\"password\":\"mypassword\"}";

        mockMvc.perform(put("/api/v1/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Tran Van B")))
                .andExpect(jsonPath("$.email", is("vanb@example.com")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("PUT /api/v1/customers/login - Login failed returns error message")
    void testLoginFailed() throws Exception {
        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .fullName("Le Van C")
                .email("vanc@example.com")
                .password("correctpassword")
                .build();
        customerService.register(request);

        String wrongLoginPayload = "{\"email\":\"vanc@example.com\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(put("/api/v1/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("email or password incorrect")));
    }
}
