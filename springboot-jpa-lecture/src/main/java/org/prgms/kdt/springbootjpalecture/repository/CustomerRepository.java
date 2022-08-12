package org.prgms.kdt.springbootjpalecture.repository;

import org.prgms.kdt.springbootjpalecture.repository.domain.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
