package org.prgms.kdt.springbootjpalecture.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prgms.kdt.springbootjpalecture.repository.domain.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void INSERT_TEST() {
        // Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("ayoung");
        customer.setLastName("kim");
        customer.setAge(24);

        // When
        repository.save(customer);

        // Then
        CustomerEntity customerEntity = repository.findById(1L).get();
        log.info("Got customer - name : {} {}, age : {}", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getAge());
    }

    @Test
    @Transactional
    void UPDATE_TEST() {
        // Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("ayoung");
        customer.setLastName("kim");
        customer.setAge(24);
        repository.save(customer);

        // When
        CustomerEntity customerEntity = repository.findById(1L).get();
        customerEntity.setFirstName("donghyun");
        customerEntity.setLastName("park");
        customer.setAge(26);

        // Then
        CustomerEntity updated = repository.findById(1L).get();
        log.info("Got customer - name : {} {}, age : {}", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getAge());
    }

}