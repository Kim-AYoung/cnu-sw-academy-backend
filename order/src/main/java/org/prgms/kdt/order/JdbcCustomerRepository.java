package org.prgms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    public static void main(String[] args) {
        try (
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "");
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("select * from customers");
        ){
            while(resultSet.next()){
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                var name = resultSet.getString("name");
                logger.info("customer id -> {}, name -> {}", customerId, name);
            }
        } catch (SQLException exception) {
            logger.error("Got error while connection", exception);
        }
    }
}
