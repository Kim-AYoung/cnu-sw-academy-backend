package org.prgms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";
    private final String SELECT_ALL_SQL = "select * from customers";
    private final String INSERT_SQL = "INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?), ?, ?)";
    private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
    private final String DELETE_ALL_SQL = "DELETE FROM customers";

    public List<String> findNames(String name) {
        List<String> names = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ){
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerId = toUUID(resultSet.getBytes("customer_id"));
                    var customerName = resultSet.getString("name");
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    names.add(customerName);
                }
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }

        return names;
    }

    public List<String> findAllNames() {
        List<String> names = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery();
        ){
            while (resultSet.next()) {
                var customerId = toUUID(resultSet.getBytes("customer_id"));
                var customerName = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                names.add(customerName);
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }

        return names;
    }

    public List<UUID> findAllIds() {
        List<UUID> uuids = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery();
        ){
            while (resultSet.next()) {
                var customerId = toUUID(resultSet.getBytes("customer_id"));
                var customerName = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                uuids.add(customerId);
            }

        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }

        return uuids;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(INSERT_SQL);
        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            statement.setString(2, name);
            statement.setString(3, email);
            return statement.executeUpdate();
        }catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }
        return 0;
    }

    static UUID toUUID(byte[] bytes) { // 4버전의 UUID로 형변환 하기 위해
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    public int updateCustomerName(UUID customerId, String name) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
        ) {
            statement.setString(1, name);
            statement.setBytes(2, customerId.toString().getBytes());
            return statement.executeUpdate();
        }catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }
        return 0;
    }

    public int deleteAllCustomers() {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {
            return statement.executeUpdate();
        } catch (SQLException throwable) {
            logger.error("Got error while closing connection", throwable);
        }
        return 0;
    }

    public static void main(String[] args) {
        var customerRepository = new JdbcCustomerRepository();

        var count = customerRepository.deleteAllCustomers();
        logger.info("deleted count -> {}", count);

        var customerId = UUID.randomUUID();
        var customerName = "new-user";
        logger.info("created customerId -> {}, customerName -> {}", customerId, customerName);

        customerRepository.insertCustomer(customerId, "new-user", "new-user@gmail.com");
        customerRepository.findAllIds().forEach(v -> logger.info("Found customerId : {}", v));

        customerRepository.findNames(customerName).forEach(v -> logger.info("Found customerName by name={} : {}", customerName, v));

        customerRepository.findAllNames().forEach(v -> logger.info("(before update) Found customerName : {}", v));
        var newCustomerName = "updated-user";
        customerRepository.updateCustomerName(customerId, newCustomerName);
        customerRepository.findAllNames().forEach(v -> logger.info("(after update) Found customerName : {}", v));
    }
}
