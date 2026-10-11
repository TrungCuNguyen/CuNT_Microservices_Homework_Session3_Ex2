package com.example.customerservice.service.impl;

import com.example.customerservice.dto.CustomerRequestDTO;
import com.example.customerservice.dto.CustomerResponseDTO;
import com.example.customerservice.dto.LoginRequestDTO;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.exception.InvalidCredentialsException;
import com.example.customerservice.exception.ResourceNotFoundException;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponseDTO register(CustomerRequestDTO requestDTO) {
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email " + requestDTO.getEmail() + " đã tồn tại trong hệ thống!");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        Customer customer = Customer.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .password(encodedPassword)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(savedCustomer);
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng với ID " + id + " không tồn tại!"));

        return mapToResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO login(LoginRequestDTO loginDTO) {
        Customer customer = customerRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("email or password incorrect"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException("email or password incorrect");
        }

        return mapToResponseDTO(customer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng với ID " + id + " không tồn tại!"));

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(requestDTO.getEmail())) {
                throw new IllegalArgumentException("Email " + requestDTO.getEmail() + " đã tồn tại trong hệ thống!");
            }
            customer.setEmail(requestDTO.getEmail());
        }

        if (requestDTO.getFullName() != null) {
            customer.setFullName(requestDTO.getFullName());
        }

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            customer.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng với ID " + id + " không tồn tại!"));

        customerRepository.delete(customer);
    }

    private CustomerResponseDTO mapToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .build();
    }
}
