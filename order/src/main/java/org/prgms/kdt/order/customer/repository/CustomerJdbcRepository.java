package org.prgms.kdt.order.customer.repository;

import org.prgms.kdt.order.JdbcCustomerRepository;
import org.prgms.kdt.order.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
            var customerId = toUUID(rs.getBytes("customer_id"));
            var customerName = rs.getString("name");
            var email = rs.getString("email");
            var lastLoginAt = rs.getTimestamp("last_login_at") != null ? rs.getTimestamp("last_login_at").toLocalDateTime() : null;
            var createdAt = rs.getTimestamp("created_at").toLocalDateTime();

            return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
    };

    @Override
    public Customer insert(Customer customer) {
        var update = jdbcTemplate.update("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UNHEX(REPLACE(?, '-', '')), ?, ?, ?)",
                customer.getCustomerId().toString().getBytes(),
                customer.getName(),
                customer.getEmail(),
                Timestamp.valueOf(customer.getCreatedAt()));
        if(update != 1) throw new RuntimeException("Noting was inserted");
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var update = jdbcTemplate.update("UPDATE customers SET name = ?, last_login_at = ? WHERE customer_id = UNHEX(REPLACE(?, '-', ''))",
                customer.getName(),
                customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null,
                customer.getCustomerId().toString().getBytes());
        if(update != 1) throw new RuntimeException("Noting was updated");
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", customerRowMapper);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where customer_id = UNHEX(REPLACE(?, '-', ''))", customerRowMapper, customerId.toString().getBytes()));
        }catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where name = ?", customerRowMapper, name));
        }catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email)   {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where email = ?", customerRowMapper, email));
        }catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers");
    }

    static UUID toUUID(byte[] bytes) { // 4버전의 UUID로 형변환 하기 위해
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
