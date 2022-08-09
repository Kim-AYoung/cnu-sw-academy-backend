package org.prgms.kdt.order.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/api/v1/customers")
    @ResponseBody
    public List<Customer> findCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers")
    public String viewCustomersPage(Model model) {
        var allCustomers = customerService.getAllCustomers();
        model.addAttribute("serverTime", LocalDateTime.now());
        model.addAttribute("customers", allCustomers);
        return "views/customers";
    }

    @GetMapping("/customers/{customerId}")
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model) {
        var customer = customerService.getCustomer(customerId);
        if(customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "views/customer-details";
        } else {
            return "views/404";
        }
    }

    @GetMapping("/customers/new")
    public String viewNewCustomerPage() {
        return "views/new-customer";
    }

    @PostMapping("/customers/new")
    public String addNewCustomer(CreateCustomerRequest createCustomerRequest) {
        customerService.createCustomer(createCustomerRequest.name(), createCustomerRequest.email());
        return "redirect:/customers";
    }
}
