package org.prgms.kdt.order.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDto(
        UUID customerId,
        String name,
        String email,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
    static CustomerDto of(Customer customer) {
        return new CustomerDto(customer.getCustomerId(),
                customer.getName(),
                customer.getEmail(),
                customer.getLastLoginAt(),
                customer.getCreatedAt());
    }

    // 해당 메소드의 존재 유무는 취향것 (Service에서 Customer로 변환 -> to X vs Controller에서 Customer로 변환 -> to O)
    static Customer to(CustomerDto dto) {
        return new Customer(dto.customerId(),
                dto.name(),
                dto.email(),
                dto.lastLoginAt(),
                dto.createdAt());
    }
}
