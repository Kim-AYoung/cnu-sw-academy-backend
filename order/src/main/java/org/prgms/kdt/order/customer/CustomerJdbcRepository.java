package org.prgms.kdt.order.customer;

import org.prgms.kdt.order.JdbcCustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    private static DataSource dataSource;

    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer) {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("INSERT INTO customers(customer_id, name, email, created_at) VALUES (UUID_TO_BIN(?), ?, ?, ?)");
        ) {
            statement.setBytes(1, customer.getCustomerId().toString().getBytes());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            var executeUpdate = statement.executeUpdate();
            if(executeUpdate != 1) throw new RuntimeException("Noting was inserted");
            return customer;
        }catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Customer update(Customer customer) {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("UPDATE customers SET name = ?, last_login_at = ? WHERE customer_id = UUID_TO_BIN(?)");
        ) {
            statement.setString(1, customer.getName());
            statement.setTimestamp(2, customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
            statement.setBytes(3, customer.getCustomerId().toString().getBytes());
            var executeUpdate = statement.executeUpdate();
            if(executeUpdate != 1) throw new RuntimeException("Noting was updated");
            return customer;
        }catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers");
                var resultSet = statement.executeQuery();
        ){
            while (resultSet.next()) {
                mapToCustomer(resultSet, allCustomers);
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }

        return allCustomers;
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where customer_id = UUID_TO_BIN(?)");
        ){
            statement.setBytes(1, customerId.toString().getBytes());
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(resultSet, allCustomers);
                }
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByName(String name) {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where name = ?");
        ){
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(resultSet, allCustomers);
                }
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email)   {
        List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where email = ?");
        ){
            statement.setString(1, email);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(resultSet, allCustomers);
                }
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }

        return allCustomers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("DELETE FROM customers");
        ) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
            throw new RuntimeException(exception);
        }
    }

    static UUID toUUID(byte[] bytes) { // 4버전의 UUID로 형변환 하기 위해
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    private void mapToCustomer(ResultSet resultSet, List<Customer> customers) throws SQLException {
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ? resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

        customers.add(new Customer(customerId, customerName, email, lastLoginAt, createdAt));
    }
}
