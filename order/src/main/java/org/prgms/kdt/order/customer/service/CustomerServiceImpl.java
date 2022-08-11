package org.prgms.kdt.order.customer.service;

import org.prgms.kdt.order.customer.model.Customer;
import org.prgms.kdt.order.customer.repository.CustomerRepository;
import org.prgms.kdt.order.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }

    @Override
    public Customer createCustomer(String name, String email) {
        var customer = new Customer(UUID.randomUUID(), name, email, LocalDateTime.now());
        return customerRepository.insert(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomer(UUID customerId) {
        return customerRepository.findById(customerId);
    }
}
