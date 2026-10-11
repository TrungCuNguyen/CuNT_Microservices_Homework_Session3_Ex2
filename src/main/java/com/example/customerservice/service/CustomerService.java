package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerRequestDTO;
import com.example.customerservice.dto.CustomerResponseDTO;
import com.example.customerservice.dto.LoginRequestDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO register(CustomerRequestDTO requestDTO);
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO login(LoginRequestDTO loginDTO);
    List<CustomerResponseDTO> getAllCustomers();
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);
    void deleteCustomer(Long id);
}
