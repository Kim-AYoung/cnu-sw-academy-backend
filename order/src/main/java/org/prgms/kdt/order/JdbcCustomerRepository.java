package org.prgms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(RealJdbcCustomerRepository.class);
    private final String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";

    public List<String> findNames(String name) {
        List<String> names = new ArrayList<>();

        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
                var statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ){
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var customerName = resultSet.getString("name");
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    logger.info("customer id -> {}, name -> {}, created At -> {}", customerId, name, createdAt);
                    names.add(customerName);
                }
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }

        return names;
    }

    public static void main(String[] args) {
        var names = new RealJdbcCustomerRepository().findNames("tester01");
        names.forEach(v -> logger.info("Found name -> {}", v));
    }
}
