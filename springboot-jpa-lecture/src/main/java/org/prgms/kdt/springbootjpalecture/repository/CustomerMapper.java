package org.prgms.kdt.springbootjpalecture.repository;

import org.apache.ibatis.annotations.Mapper;
import org.prgms.kdt.springbootjpalecture.repository.domain.Customer;

@Mapper
public interface CustomerMapper {
    void save(Customer customer);
    Customer findById(long id);
}