package org.prgms.kdt.order.customer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(value = "/customers")
    public ModelAndView findCustomers() {
        var allCustomers = customerService.getAllCustomers();
        return new ModelAndView("views/customers",
                Map.of("serverTime", LocalDateTime.now(),
                        "customers", allCustomers));
    }
}
