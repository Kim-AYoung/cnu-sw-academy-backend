package org.prgms.kdt.order.customer;

import java.util.List;

public interface CustomerService {

    void createCustomers(List<Customer> customers);

    List<Customer> getAllCustomers();

    Customer createCustomer(String name, String email);
}
