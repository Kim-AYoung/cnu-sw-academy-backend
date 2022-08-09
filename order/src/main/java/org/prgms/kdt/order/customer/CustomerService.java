package org.prgms.kdt.order.customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    void createCustomers(List<Customer> customers);

    Customer createCustomer(String name, String email);

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomer(UUID customerId);
}
